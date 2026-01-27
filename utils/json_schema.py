import json
from jsonschema import validators


def is_valid_json_schema_definition(json_schema: str) -> bool:
    try:
        json_schema_dict = json.loads(json_schema)
        validator_cls = validators.validator_for(json_schema_dict)
        validator_cls.check_schema(json_schema_dict)
        return True
    except:
        return False