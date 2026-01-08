package com.ai.stocks.api.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // --------- 400: invalid input (CSV parsing, general validation) ---------

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        return respond(HttpStatus.BAD_REQUEST, "Invalid request", ex.getMessage(), req, List.of(), ex, false);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgNotValid(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ApiErrorDetail> details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ApiErrorDetail(fe.getField(), fe.getDefaultMessage()))
                .toList();

        return respond(HttpStatus.BAD_REQUEST, "Validation failed", "Request body validation failed.", req, details, ex, false);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        List<ApiErrorDetail> details = ex.getConstraintViolations().stream()
                .map(v -> new ApiErrorDetail(v.getPropertyPath().toString(), v.getMessage()))
                .toList();

        return respond(HttpStatus.BAD_REQUEST, "Validation failed", "Request parameter validation failed.", req, details, ex, false);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleBadJson(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return respond(HttpStatus.BAD_REQUEST, "Malformed JSON", "Request body could not be parsed.", req, List.of(), ex, false);
    }

    // --------- 404: not found ---------

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(NoSuchElementException ex, HttpServletRequest req) {
        return respond(HttpStatus.NOT_FOUND, "Not found", ex.getMessage(), req, List.of(), ex, false);
    }

    // --------- 415: content type issues ---------

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleUnsupportedMedia(HttpMediaTypeNotSupportedException ex, HttpServletRequest req) {
        String msg = "Unsupported Content-Type. Use application/json or multipart/form-data as required by the endpoint.";
        return respond(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type", msg, req, List.of(), ex, false);
    }

    // --------- 400/413: multipart upload issues ---------

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleMaxUpload(MaxUploadSizeExceededException ex, HttpServletRequest req) {
        String msg = "Uploaded file is too large. Reduce file size or increase upload limit.";
        return respond(HttpStatus.PAYLOAD_TOO_LARGE, "File too large", msg, req, List.of(), ex, false);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ApiErrorResponse> handleMultipart(MultipartException ex, HttpServletRequest req) {
        String msg = "Multipart request is invalid. Make sure you send form-data with a 'file' part.";
        return respond(HttpStatus.BAD_REQUEST, "Invalid multipart request", msg, req, List.of(), ex, false);
    }

    // --------- 500: last resort ---------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnknown(Exception ex, HttpServletRequest req) {
        // Log full stacktrace for server-side debugging
        return respond(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error",
                "An unexpected error occurred.", req, List.of(), ex, true);
    }

    // --------- helper ---------

    private ResponseEntity<ApiErrorResponse> respond(
            HttpStatus status,
            String error,
            String message,
            HttpServletRequest req,
            List<ApiErrorDetail> details,
            Exception ex,
            boolean logStacktrace
    ) {
        if (logStacktrace) {
            log.error("Unhandled exception at {} {}", req.getMethod(), req.getRequestURI(), ex);
        } else {
            log.warn("Handled exception at {} {}: {}", req.getMethod(), req.getRequestURI(), ex.toString());
        }

        ApiErrorResponse body = new ApiErrorResponse(
                OffsetDateTime.now(),
                status.value(),
                error,
                message,
                req.getRequestURI(),
                details == null ? List.of() : details
        );
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(body);
    }
}
