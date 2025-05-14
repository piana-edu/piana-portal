package ir.piana.boot.spuerapp.servlet;

import ir.piana.portal.common.flags.FlagType;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Set;
import java.util.stream.Collectors;

public class StripPrefixGatewayServlet extends HttpServlet {
    protected int stripPrefix;
    protected String baseUrl;
    protected RestClient restClient;

    private final Set<String> hopByHopHeaders = Set.of(
            "connection", "keep-alive", "proxy-authenticate", "proxy-authorization",
            "te", "trailer", "transfer-encoding", "upgrade"
    );

    public StripPrefixGatewayServlet(RestClient restClient, String baseUrl, int stripPrefix) {
        this.restClient = restClient;
        this.baseUrl = baseUrl;
        this.stripPrefix = stripPrefix;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doForward(req, resp, HttpMethod.GET);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doForward(req, resp, HttpMethod.HEAD);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doForward(req, resp, HttpMethod.POST);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doForward(req, resp, HttpMethod.PUT);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doForward(req, resp, HttpMethod.DELETE);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doForward(req, resp, HttpMethod.OPTIONS);
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doForward(req, resp, HttpMethod.TRACE);
    }

    protected void doForward(HttpServletRequest req, HttpServletResponse resp, HttpMethod httpMethod) {
//        String servletPath = req.getServletPath();
//        int nthOccurrence = findNthOccurrence(servletPath, '/', stripPrefix);
//        String substring = servletPath.substring(nthOccurrence);

        String url = baseUrl + req.getPathInfo();
        if (!req.getParameterMap().isEmpty()) {
            StringBuilder query = new StringBuilder("?");
            req.getParameterMap().forEach((key, values) -> {
                for (String value : values) {
                    query.append(key).append("=").append(value).append("&");
                }
            });
            url += query.substring(0, query.length() - 1); // remove trailing &
        }

        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            String value = req.getHeader(header);
            headers.add(header, value);
        }

        ResponseEntity<byte[]> backendResponse = switch (httpMethod.name()) {
            case "GET" ->
                    call(restClient.get().uri(url).headers(httpHeaders -> httpHeaders.addAll(headers)).retrieve());
            case "HEAD" ->
                    call(restClient.head().uri(url).headers(httpHeaders -> httpHeaders.addAll(headers)).retrieve());
            case "POST" -> {
                byte[] body;
                try {
                    body = req.getInputStream().readAllBytes();
                } catch (IOException e) {
                    e.printStackTrace();
                    yield null;
                }
                yield call(restClient.post().uri(url).headers(httpHeaders -> httpHeaders.addAll(headers)).body(body).retrieve());
            }
            case "PUT" -> {
                byte[] body;
                try {
                    body = req.getInputStream().readAllBytes();
                } catch (IOException e) {
                    e.printStackTrace();
                    yield null;
                }
                yield call(restClient.put().uri(url).headers(httpHeaders -> httpHeaders.addAll(headers)).body(body).retrieve());
            }
            case "PATCH" ->
                    call(restClient.patch().uri(url).headers(httpHeaders -> httpHeaders.addAll(headers)).retrieve());
            case "DELETE" ->
                    call(restClient.delete().uri(url).headers(httpHeaders -> httpHeaders.addAll(headers)).retrieve());
            case "OPTIONS" ->
                    call(restClient.options().uri(url).headers(httpHeaders -> httpHeaders.addAll(headers)).retrieve());
            default -> null;
        };
        if (backendResponse != null) {
            HttpStatusCode status = backendResponse.getStatusCode();
            HttpHeaders responseHeaders = backendResponse.getHeaders();
            byte[] body = backendResponse.getBody();
            resp.setStatus(status.value());
            responseHeaders.entrySet().forEach(header -> {
                if (header.getKey().equalsIgnoreCase("portal-flag")) {
                    if (FlagType.byFlag(header.getValue().getFirst()) == FlagType.NEED_TO_CHANGE_AUTHENTICATED_INFO) {
                        System.out.println(FlagType.NEED_TO_CHANGE_AUTHENTICATED_INFO.getFlag());
                    }
                } else if (header.getKey().equalsIgnoreCase("connection") ||
                        header.getKey().equalsIgnoreCase("Transfer-Encoding") ||
                        header.getKey().equalsIgnoreCase("Keep-Alive")) {
                } else {
                    resp.addHeader(header.getKey(), header.getValue().stream().collect(Collectors.joining(",")));
                }
            });
            try {
                try (ServletOutputStream out = resp.getOutputStream()) {
                    out.write(body);
                    out.flush(); // 👈 important!
                } // 👈 this also closes the stream
//                resp.flushBuffer();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            resp.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            try {
                resp.getOutputStream().write("internal error occurred!".getBytes());
            } catch (IOException e) {
            }
        }
    }

    ResponseEntity<byte[]> call(RestClient.ResponseSpec responseSpec) {
        try {
            return responseSpec.onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                throw new HttpClientErrorException(
                        HttpStatus.BAD_REQUEST, "Server Error: " + response.getStatusCode(),
                        response.getHeaders(), response.getBody().readAllBytes(), StandardCharsets.UTF_8);
            }).onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                throw new HttpServerErrorException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Server Error: " + response.getStatusCode(),
                        response.getHeaders(), response.getBody().readAllBytes(), StandardCharsets.UTF_8);
            }).toEntity(byte[].class);
        } catch (HttpServerErrorException | HttpClientErrorException e) {
            return new ResponseEntity<>(e.getResponseBodyAsByteArray(), e.getResponseHeaders(), e.getStatusCode());
        }
    }

    public static int findNthOccurrence(String str, char ch, int n) {
        int pos = -1;
        int fromIndex = 0;

        for (int i = 0; i < n; i++) {
            pos = str.indexOf(ch, fromIndex);
            if (pos == -1) {
                return -1; // not found
            }
            fromIndex = pos + 1;
        }

        return pos;
    }
}
