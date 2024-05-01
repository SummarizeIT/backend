package io.summarizeit.backend.util;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class Constants {
    
    public static String backendUrl;

    @Value("${app.backend-url}")
    public void setBackendUrl(String url){
        backendUrl = url;
    }

    public static final int PAGE_SIZE = 20;

    public static final String SECURITY_SCHEME_NAME = "bearerAuth";

    public static final String TOKEN_HEADER = "Authorization";

    public static final String TOKEN_TYPE = "Bearer";

    public static final String ADMIN_ROLE_NAME = "Admin";

    public static final int PASSWORD_RESET_TOKEN_LENGTH = 32;

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    public static String getTokenFromPath(final String path) {
        if (path == null || path.isEmpty())
            return null;

        final String[] fields = path.split("/");

        if (fields.length == 0)
            return null;

        try {
            return fields[2];
        } catch (final IndexOutOfBoundsException e) {
            System.out.println("Cannot find user or channel id from the path!. Ex:" + e.getMessage());
        }
        return null;
    }
    
    public static String getStaticFileUrl(UUID file){
        return String.format("%s/public/%s", backendUrl, file);
    }
}
