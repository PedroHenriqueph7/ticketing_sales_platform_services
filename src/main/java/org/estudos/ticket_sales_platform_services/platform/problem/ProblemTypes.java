package org.estudos.ticket_sales_platform_services.platform.problem;
import java.net.URI;

public final class ProblemTypes {
    public static final String INVALID_DATA = "https://api.suaplataforma.com/errors/invalid-data";
    public static final String INVALID_INPUT = "https://api.suaplataforma.com/errors/invalid-input";
    public static final String INVALID_DATE = "https://api.suaplataforma.com/errors/invalid-date";
    public static final String CATEGORY_NOT_FOUND = "https://api.suaplataforma.com/errors/category-not-found";
    public static final String PRODUCER_NOT_FOUND = "https://api.suaplataforma.com/errors/producer-not-found";
    public static final String USER_NOT_FOUND = "https://api.suaplataforma.com/errors/user-not-found";
    public static final String INTERNAL_ERROR = "https://api.suaplataforma.com/errors/internal-error";
    private ProblemTypes() {
    }
    public static URI uri(String type) {
        return URI.create(type);
    }
}
