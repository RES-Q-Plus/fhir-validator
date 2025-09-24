Once you have cloned the repository, run the following command to build and start the containers:

```bash 
docker compose up
```

# Ports

Snowstorm: http://localhost:8081/swagger-ui/index.html
Validator API: http://localhost:8085/swagger-ui/index.html


# Validator

For the API to work correctly, you must upload a valid FHIR Bundle in JSON format.

For example : 
```json
{
   "resourceType":"Bundle",
   "type":"transaction",
   "entry":[
      {
         "fullUrl":"urn:uuid:6cfa111e-591b-424a-baee-43aa0c9cf157",
         "resource":{
            "resourceType":"Patient",
            "extension":[
               {
                  "url":"http://testSK.org/StructureDefinition/gender-snomed-ext",
                  "valueCodeableConcept":{
                     "coding":[
                        {
                           "system":"http://snomed.info/sct",
                           "code":"248153007",
                           "display":"Male (finding)"
                        }
                     ]
                  }
               },
               {
                  "url":"http://testSK.org/StructureDefinition/patient-age-ext",
                  "valueInteger":59
               }
            ]
         },
         "request":{
            "method":"POST",
            "url":"Patient"
         }
      }
   ]
}
```

# SNOWSTORM
To upload SNOMED CT codes, see https://github.com/IHTSDO/snowstorm/blob/master/docs/loading-snomed.md.

If you don't upload the SNOMED CT codes, the validator will show errors related to SNOMED CT.

