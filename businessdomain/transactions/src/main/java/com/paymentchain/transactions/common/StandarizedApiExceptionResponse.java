package com.paymentchain.transactions.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "This model is created to return errors")
@NoArgsConstructor
@Data
public class StandarizedApiExceptionResponse {
    @Schema(description = "The unique URI identifier that categorizes the error", name = "type",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "/errors/authentication/not-authorized")
    private String type;

    @Schema(description = "A brief, human-readable message about error", name = "title",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "The user doesnt have authentication")
    private String title;

    @Schema(description = "The unique error code", name = "code",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "192")
    private String code;

    @Schema(description = "A human-readable explanation of the error", name = "detail",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "The user doesnt have the propertly permissions to access")
    private String detail;

    @Schema(description = "A URI that identifies the specific ocurrence of the error", name = "instance",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "/errors/authentication/not-authorized/01")
    private String instance;

    public StandarizedApiExceptionResponse(String type, String title, String code, String detail, String instance) {
        super();
        this.type = type;
        this.title = title;
        this.code = code;
        this.detail = detail;
        this.instance = instance;
    }
}
