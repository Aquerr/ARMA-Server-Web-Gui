package pl.bartlomiejstepien.armaserverwebgui.application.config.logging;

import org.springframework.http.HttpHeaders;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Strategy;

import java.io.IOException;
import java.util.List;

public class AswgLogBookStrategy implements Strategy
{
    private static final String[] URI_PATTERNS_WITH_SKIPPED_RESPONSE_BODY = new String[] {
            "/api/v1/logging/latest-logs",
            "/api/v1/logging/logs-sse",
            "/api/v1/missions-files"
    };

    @Override
    public HttpResponse process(HttpRequest request, HttpResponse response) throws IOException
    {
        if (shouldIgnoreResponseBody(request, response))
        {
            return response.withoutBody();
        }
        return response.withBody();
    }

    private boolean shouldIgnoreResponseBody(HttpRequest httpRequest, HttpResponse response)
    {
        if (shouldIgnoreBodyByContentType(response))
            return true;

        if (shouldIgnoreBodyByRequestUri(httpRequest))
            return true;

        return false;
    }

    private boolean shouldIgnoreBodyByContentType(HttpResponse response)
    {
        List<String> headers = response.getHeaders().get(HttpHeaders.CONTENT_TYPE);

        if (headers != null && !headers.isEmpty())
        {
            for (String ignoredFileContent : LogbookConfig.CONTENT_TYPES_WITH_SKIPPED_RESPONSE_BODY)
            {
                if (headers.contains(ignoredFileContent))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean shouldIgnoreBodyByRequestUri(HttpRequest request)
    {
        String requestPath = request.getPath();
        for (String uriPattern : URI_PATTERNS_WITH_SKIPPED_RESPONSE_BODY)
        {
            if (requestPath.startsWith(uriPattern))
            {
                return true;
            }
        }
        return false;
    }
}
