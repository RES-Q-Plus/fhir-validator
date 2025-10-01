package com.example.validator.validation;

import ca.uhn.fhir.validation.IValidationContext;
import ca.uhn.fhir.validation.IValidatorModule;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.Resource;
import org.hl7.fhir.r5.model.ResourceType;

import java.util.EnumSet;
import java.util.Set;

public class RequiredResourcesValidator implements IValidatorModule {

  // Tipos mínimos exigidos en el Bundle
  private static final Set<ResourceType> REQUIRED = EnumSet.of(
      ResourceType.Patient, ResourceType.Encounter, ResourceType.Condition, ResourceType.Organization
  );

  @Override
  public void validateResource(IValidationContext<IBaseResource> ctx) {
    Resource resource = (Resource) ctx.getResource();

    // Exigimos que el recurso raíz sea un Bundle con los tipos mínimos
    if (!(resource instanceof Bundle)) {
      addError(ctx, "El recurso raíz debe ser un Bundle que contenga al menos: Patient, Encounter, Condition y Organization.");
      return;
    }

    Bundle bundle = (Bundle) resource;
    EnumSet<ResourceType> present = EnumSet.noneOf(ResourceType.class);

    bundle.getEntry().forEach(e -> {
      if (e.getResource() != null) {
        present.add(e.getResource().getResourceType());
      }
    });

    EnumSet<ResourceType> missing = EnumSet.copyOf(REQUIRED);
    missing.removeAll(present);

    if (!missing.isEmpty()) {
      addError(ctx, "Faltan recursos obligatorios en el Bundle: " + missing);
    }
  }

  private void addError(IValidationContext<?> ctx, String message) {
    SingleValidationMessage msg = new SingleValidationMessage();
    msg.setSeverity(ResultSeverityEnum.ERROR);
    msg.setMessage(message);
    ctx.addValidationMessage(msg);
  }
}
