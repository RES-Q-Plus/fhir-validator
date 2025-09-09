package com.example.validator.config.api;

import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * Simple REST controller exposing a validation endpoint.
 *
 * POST /api/validate/bundle
 *  - Accepts a JSON FHIR R5 Bundle as the request body
 *  - Validates it using the configured FhirValidator
 *  - Returns an OperationOutcome (as JSON) with issues/warnings/errors
 */
@RestController
@RequestMapping("/api/validate")
public class ValidationController {

  /** HAPI FHIR validator composed in configuration (R5 + custom SNOMED module). */
  private final FhirValidator validator;

  /** JSON parser bound to the same R5 FhirContext (configured in FhirValidatorConfig). */
  private final IParser json;

  public ValidationController(FhirValidator validator, IParser json) {
    this.validator = validator;
    this.json = json;
  }

  /**
   * Validate a FHIR Bundle.
   *
   * @param bundleJson raw JSON string containing a FHIR R5 Bundle
   * @return OperationOutcome serialized as JSON (string)
   *
   * Notes:
   *  - We parse the incoming string into an R5 Bundle using the injected parser.
   *  - validator.validateWithResult(...) runs all registered validator modules.
   *  - ValidationResult#toOperationOutcome produces a standard OperationOutcome.
   *  - We serialize the OperationOutcome back to JSON and return it.
   */
  @PostMapping(
      value = "/bundle",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public String validateBundle(@RequestBody String bundleJson) {
    // Parse request JSON into a FHIR R5 Bundle resource
    Bundle bundle = (Bundle) json.parseResource(bundleJson);

    // Validate using the configured FhirValidator (core chain + custom SNOMED validator)
    String withNarrJson = json.encodeResourceToString(bundle);        // genera <text> si falta
    Bundle bundleWithNarr = (Bundle) json.parseResource(withNarrJson);

    ValidationResult vr = validator.validateWithResult(bundleWithNarr);

    // Convert the result to a standard OperationOutcome
    OperationOutcome oo = (OperationOutcome) vr.toOperationOutcome();

    // Return the OperationOutcome JSON
    return json.encodeResourceToString(oo);
  }
}
