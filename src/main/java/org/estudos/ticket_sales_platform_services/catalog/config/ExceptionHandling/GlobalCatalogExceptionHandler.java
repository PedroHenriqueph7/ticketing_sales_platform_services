package org.estudos.ticket_sales_platform_services.catalog.config.ExceptionHandling;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.CategoryNotFoundException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.InputObjectInvalidException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.InvalidDateException;
import org.estudos.ticket_sales_platform_services.identity.application.core.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;
@RestControllerAdvice(basePackages = "org.estudos.ticket_sales_platform_services.catalog")
public class GlobalCatalogExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalCatalogExceptionHandler.class);

    @ExceptionHandler(CategoryNotFoundException.class)
    public ProblemDetail handleCategoryNotFoundException(CategoryNotFoundException ex) {
        log.warn("Regra de negócio violada - categoria não encontrada: {}", ex.getMessage());

        return problem(
                HttpStatus.NOT_FOUND,
                "Categoria não encontrada",
                ex.getMessage()
        );
    }

    @ExceptionHandler(InputObjectInvalidException.class)
    public ProblemDetail handleInputObjectInvalidException(InputObjectInvalidException ex) {
        log.warn("Regra de negócio violada - objeto de entrada inválido: {}", ex.getMessage());

        return problem(
                HttpStatus.BAD_REQUEST,
                "Violação de Regra de Negócio",
                ex.getMessage()
        );
    }

    @ExceptionHandler(InvalidDateException.class)
    public ProblemDetail handleInvalidDateException(InvalidDateException ex) {
        log.warn("Regra de negócio violada - data inválida: {}", ex.getMessage());

        return problem(
                HttpStatus.BAD_REQUEST,
                "Violação de Regra de Negócio - Data inválida",
                ex.getMessage()
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleProducerNotFoundException(UserNotFoundException ex) {
        log.warn("Regra de negócio violada - produtora não encontrada: {}", ex.getMessage());

        return problem(
                HttpStatus.NOT_FOUND,
                "Produtora não encontrada",
                ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Erro de validação nos dados enviados: {} erro(s) de campo", ex.getBindingResult().getFieldErrorCount());

        ProblemDetail problemDetail = problem(
                HttpStatus.BAD_REQUEST,
                "Dados Inválidos",
                "Erro de validação nos dados enviados."
        );

        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Valor inválido",
                        (mensagemExistente, novaMensagem) -> mensagemExistente
                ));

        problemDetail.setProperty("invalid_params", fieldErrors);
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUncaughtException(Exception ex) {

        log.error("Erro interno não tratado: ", ex);
        return problem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro Interno do Servidor",
                "Ocorreu um erro interno inesperado. Tente novamente mais tarde."
        );
    }

    private static ProblemDetail problem(HttpStatus status, String title, String detail) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }
}
