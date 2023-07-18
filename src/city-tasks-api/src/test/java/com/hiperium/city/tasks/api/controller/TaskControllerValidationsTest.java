package com.hiperium.city.tasks.api.controller;

import com.hiperium.city.tasks.api.common.AbstractContainerBaseTest;
import com.hiperium.city.tasks.api.dto.ErrorDetailsDTO;
import com.hiperium.city.tasks.api.dto.TaskOperationDTO;
import com.hiperium.city.tasks.api.utils.TaskUtil;
import com.hiperium.city.tasks.api.utils.enums.EnumLanguageCode;
import com.hiperium.city.tasks.api.utils.enums.EnumTaskOperation;
import com.hiperium.city.tasks.api.utils.enums.EnumValidationError;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.ZonedDateTime;

import static com.hiperium.city.tasks.api.utils.PathsUtil.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@ActiveProfiles("test")
@TestInstance(PER_CLASS)
@AutoConfigureWebTestClient
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerValidationsTest extends AbstractContainerBaseTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("Create Task without name")
    void givenTaskWithoutName_whenCreate_thenReturnError404() {
        TaskOperationDTO taskOperationDto = TaskUtil.getTaskOperationTemplate();
        taskOperationDto.setOperation(EnumTaskOperation.CREATE);
        taskOperationDto.getTask().setName(null);
        this.getValidationErrorResponse(taskOperationDto, EnumLanguageCode.EN)
                .value(errorDetailsDTO -> {
                    Assertions.assertThat(errorDetailsDTO.getErrorCode())
                            .isEqualTo(EnumValidationError.FIELD_VALIDATION_ERROR.getCode());
                    Assertions.assertThat(errorDetailsDTO.getErrorMessage())
                            .isEqualTo("Task name cannot be empty.");
                });
    }

    @Test
    @DisplayName("Create Task without name - Spanish")
    void givenTaskWithoutName_whenCreate_thenReturnError404InSpanish() {
        TaskOperationDTO taskOperationDto = TaskUtil.getTaskOperationTemplate();
        taskOperationDto.setOperation(EnumTaskOperation.CREATE);
        taskOperationDto.getTask().setName(null);
        this.getValidationErrorResponse(taskOperationDto, EnumLanguageCode.ES)
                .value(errorDetailsDTO -> {
                    Assertions.assertThat(errorDetailsDTO.getErrorCode())
                            .isEqualTo(EnumValidationError.FIELD_VALIDATION_ERROR.getCode());
                    Assertions.assertThat(errorDetailsDTO.getErrorMessage())
                            .isEqualTo("El nombre de la tarea no puede estar vacío.");
                });
    }

    @Test
    @DisplayName("Create Task with wrong hour")
    void givenTaskWithWrongHour_whenCreate_thenReturnError404() {
        TaskOperationDTO taskOperationDto = TaskUtil.getTaskOperationTemplate();
        taskOperationDto.setOperation(EnumTaskOperation.CREATE);
        taskOperationDto.getTask().setHour(25);
        this.getValidationErrorResponse(taskOperationDto, EnumLanguageCode.EN)
                .value(errorDetailsDTO -> {
                    Assertions.assertThat(errorDetailsDTO.getErrorCode())
                            .isEqualTo(EnumValidationError.FIELD_VALIDATION_ERROR.getCode());
                    Assertions.assertThat(errorDetailsDTO.getErrorMessage())
                            .isEqualTo("Task hour must be less than or equal to 23.");
                });
    }

    @Test
    @DisplayName("Create Task with wrong hour - Spanish")
    void givenTaskWithWrongHour_whenCreate_thenReturnError404InSpanish() {
        TaskOperationDTO taskOperationDto = TaskUtil.getTaskOperationTemplate();
        taskOperationDto.setOperation(EnumTaskOperation.CREATE);
        taskOperationDto.getTask().setHour(25);
        this.getValidationErrorResponse(taskOperationDto, EnumLanguageCode.ES)
                .value(errorDetailsDTO -> {
                    Assertions.assertThat(errorDetailsDTO.getErrorCode())
                            .isEqualTo(EnumValidationError.FIELD_VALIDATION_ERROR.getCode());
                    Assertions.assertThat(errorDetailsDTO.getErrorMessage())
                            .isEqualTo("La hora de la tarea debe ser menor o igual a 23.");
                });
    }

    @Test
    @DisplayName("Create Task with wrong minute")
    void givenTaskWithWrongMinute_whenCreate_thenReturnError404() {
        TaskOperationDTO taskOperationDto = TaskUtil.getTaskOperationTemplate();
        taskOperationDto.setOperation(EnumTaskOperation.CREATE);
        taskOperationDto.getTask().setMinute(-20);
        this.getValidationErrorResponse(taskOperationDto, EnumLanguageCode.EN)
                .value(errorDetailsDTO -> {
                    Assertions.assertThat(errorDetailsDTO.getErrorCode())
                            .isEqualTo(EnumValidationError.FIELD_VALIDATION_ERROR.getCode());
                    Assertions.assertThat(errorDetailsDTO.getErrorMessage())
                            .isEqualTo("Task minute must be greater than or equal to 0.");
                });
    }

    @Test
    @DisplayName("Create Task with wrong minute - Spanish")
    void givenTaskWithWrongMinute_whenCreate_thenReturnError404InSpanish() {
        TaskOperationDTO taskOperationDto = TaskUtil.getTaskOperationTemplate();
        taskOperationDto.setOperation(EnumTaskOperation.CREATE);
        taskOperationDto.getTask().setMinute(-20);
        this.getValidationErrorResponse(taskOperationDto, EnumLanguageCode.ES)
                .value(errorDetailsDTO -> {
                    Assertions.assertThat(errorDetailsDTO.getErrorCode())
                            .isEqualTo(EnumValidationError.FIELD_VALIDATION_ERROR.getCode());
                    Assertions.assertThat(errorDetailsDTO.getErrorMessage())
                            .isEqualTo("El minuto de la tarea debe ser mayor o igual a 0.");
                });
    }

    @Test
    @DisplayName("Create Task without execution days")
    void givenTaskWithoutExecutionDays_whenCreate_thenReturnError404() {
        TaskOperationDTO taskOperationDto = TaskUtil.getTaskOperationTemplate();
        taskOperationDto.setOperation(EnumTaskOperation.CREATE);
        taskOperationDto.getTask().setExecutionDays(null);
        this.getValidationErrorResponse(taskOperationDto, EnumLanguageCode.EN)
                .value(errorDetailsDTO -> {
                    Assertions.assertThat(errorDetailsDTO.getErrorCode())
                            .isEqualTo(EnumValidationError.FIELD_VALIDATION_ERROR.getCode());
                    Assertions.assertThat(errorDetailsDTO.getErrorMessage())
                            .isEqualTo("Task execution days cannot be empty.");
                });
    }

    @Test
    @DisplayName("Create Task without execution days - Spanish")
    void givenTaskWithoutExecutionDays_whenCreate_thenReturnError404InSpanish() {
        TaskOperationDTO taskOperationDto = TaskUtil.getTaskOperationTemplate();
        taskOperationDto.setOperation(EnumTaskOperation.CREATE);
        taskOperationDto.getTask().setExecutionDays(null);
        this.getValidationErrorResponse(taskOperationDto, EnumLanguageCode.ES)
                .value(errorDetailsDTO -> {
                    Assertions.assertThat(errorDetailsDTO.getErrorCode())
                            .isEqualTo(EnumValidationError.FIELD_VALIDATION_ERROR.getCode());
                    Assertions.assertThat(errorDetailsDTO.getErrorMessage())
                            .isEqualTo("Los días de ejecución de la tarea no pueden estar vacíos.");
                });
    }

    @Test
    @DisplayName("Create Task with past execute until date")
    void givenTaskWithPastExecuteUntil_whenCreate_thenReturnError404() {
        TaskOperationDTO taskOperationDto = TaskUtil.getTaskOperationTemplate();
        taskOperationDto.setOperation(EnumTaskOperation.CREATE);
        taskOperationDto.getTask().setExecuteUntil(ZonedDateTime.now().minusDays(1));
        this.getValidationErrorResponse(taskOperationDto, EnumLanguageCode.EN)
                .value(errorDetailsDTO -> {
                    Assertions.assertThat(errorDetailsDTO.getErrorCode())
                            .isEqualTo(EnumValidationError.FIELD_VALIDATION_ERROR.getCode());
                    Assertions.assertThat(errorDetailsDTO.getErrorMessage())
                            .isEqualTo("Task execute until must be in the future.");
                });
    }

    @Test
    @DisplayName("Create Task with past execute until date - Spanish")
    void givenTaskWithPastExecuteUntil_whenCreate_thenReturnError404InSpanish() {
        TaskOperationDTO taskOperationDto = TaskUtil.getTaskOperationTemplate();
        taskOperationDto.setOperation(EnumTaskOperation.CREATE);
        taskOperationDto.getTask().setExecuteUntil(ZonedDateTime.now().minusDays(1));
        this.getValidationErrorResponse(taskOperationDto, EnumLanguageCode.ES)
                .value(errorDetailsDTO -> {
                    Assertions.assertThat(errorDetailsDTO.getErrorCode())
                            .isEqualTo(EnumValidationError.FIELD_VALIDATION_ERROR.getCode());
                    Assertions.assertThat(errorDetailsDTO.getErrorMessage())
                            .isEqualTo("La fecha final de ejecución de la tarea debe ser posterior a la fecha actual.");
                });
    }

    @Test
    @DisplayName("Create Task without Device ID")
    void givenTaskWithoutDeviceId_whenCreate_thenReturnError404() {
        TaskOperationDTO taskOperationDto = TaskUtil.getTaskOperationTemplate();
        taskOperationDto.setOperation(EnumTaskOperation.CREATE);
        taskOperationDto.getTask().setDeviceId(null);
        this.getValidationErrorResponse(taskOperationDto, EnumLanguageCode.EN)
                .value(errorDetailsDTO -> {
                    Assertions.assertThat(errorDetailsDTO.getErrorCode())
                            .isEqualTo(EnumValidationError.FIELD_VALIDATION_ERROR.getCode());
                    Assertions.assertThat(errorDetailsDTO.getErrorMessage())
                            .isEqualTo("Device ID cannot be empty.");
                });
    }

    @Test
    @DisplayName("Create Task without Device ID - Spanish")
    void givenTaskWithoutDeviceId_whenCreate_thenReturnError404inSpanish() {
        TaskOperationDTO taskOperationDto = TaskUtil.getTaskOperationTemplate();
        taskOperationDto.setOperation(EnumTaskOperation.CREATE);
        taskOperationDto.getTask().setDeviceId(null);
        this.getValidationErrorResponse(taskOperationDto, EnumLanguageCode.ES)
                .value(errorDetailsDTO -> {
                    Assertions.assertThat(errorDetailsDTO.getErrorCode())
                            .isEqualTo(EnumValidationError.FIELD_VALIDATION_ERROR.getCode());
                    Assertions.assertThat(errorDetailsDTO.getErrorMessage())
                            .isEqualTo("El ID del dispositivo no puede estar vacío.");
                });
    }

    @Test
    @DisplayName("Create Task without Device Action")
    void givenTaskWithoutDeviceAction_whenCreate_thenReturnError404() {
        TaskOperationDTO taskOperationDto = TaskUtil.getTaskOperationTemplate();
        taskOperationDto.setOperation(EnumTaskOperation.CREATE);
        taskOperationDto.getTask().setDeviceOperation(null);
        this.getValidationErrorResponse(taskOperationDto, EnumLanguageCode.EN)
                .value(errorDetailsDTO -> {
                    Assertions.assertThat(errorDetailsDTO.getErrorCode())
                            .isEqualTo(EnumValidationError.FIELD_VALIDATION_ERROR.getCode());
                    Assertions.assertThat(errorDetailsDTO.getErrorMessage())
                            .isEqualTo("Device action cannot be empty.");
                });
    }

    @Test
    @DisplayName("Create Task without Device Action - Spanish")
    void givenTaskWithoutDeviceAction_whenCreate_thenReturnError404InSpanish() {
        TaskOperationDTO taskOperationDto = TaskUtil.getTaskOperationTemplate();
        taskOperationDto.setOperation(EnumTaskOperation.CREATE);
        taskOperationDto.getTask().setDeviceOperation(null);
        this.getValidationErrorResponse(taskOperationDto, EnumLanguageCode.ES)
                .value(errorDetailsDTO -> {
                    Assertions.assertThat(errorDetailsDTO.getErrorCode())
                            .isEqualTo(EnumValidationError.FIELD_VALIDATION_ERROR.getCode());
                    Assertions.assertThat(errorDetailsDTO.getErrorMessage())
                            .isEqualTo("La acción del dispositivo no puede estar vacía.");
                });
    }

    @NotNull
    private WebTestClient.BodySpec<ErrorDetailsDTO, ?> getValidationErrorResponse(TaskOperationDTO taskOperationDto,
                                                                                  EnumLanguageCode languageEnum) {
        return this.webTestClient
                .post()
                .uri(CONTEXT_PATH + API_VERSION_1 + TASK_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT_LANGUAGE, languageEnum.getCode())
                .header(AUTHORIZATION, super.getBearerAccessToken())
                .bodyValue(taskOperationDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorDetailsDTO.class);
    }
}
