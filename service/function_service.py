from pymongo.collection import Collection

from exception.types import ClientException, NotFoundException
from service.structure_service import StructureService
from utils.json_schema import is_valid_json_schema_definition
from utils.time import current_datetime


class FunctionService:
    def __init__(self, mongo: Collection, structure_service: StructureService):
        self.mongo = mongo
        self.structure_service = structure_service

    def create(self, node_identifier: str, structure_address: str, identifier: str, display_name: str, description: str, input_json_schema: str, output_json_schema: str, creator: str):
        if not self.structure_service.exists(node_identifier, structure_address):
            raise ValueError(f"Structure {structure_address} not found on node {node_identifier}")

        if not is_valid_json_schema_definition(input_json_schema):
            raise ValueError(f"Invalid input JSON schema provided for function {identifier} of structure {structure_address} on node {node_identifier}")

        if not is_valid_json_schema_definition(output_json_schema):
            raise ValueError(f"Invalid output JSON schema provided for function {identifier} of structure {structure_address} on node {node_identifier}")

        if self.mongo.count_documents({
            "node_identifier": node_identifier,
            "structure_address": structure_address,
            "identifier": identifier
        }) > 0:
            raise ClientException(f"Function {identifier} already exists for structure {structure_address} on node {node_identifier}")

        self.mongo.insert_one({
            "node_identifier": node_identifier,
            "structure_address": structure_address,
            "identifier": identifier,
            "display_name": display_name,
            "description": description,
            "input_json_schema": input_json_schema,
            "output_json_schema": output_json_schema,
            "creator": creator,
            "created_at": current_datetime(),
            "updated_at": current_datetime()
        })

        return {
            "identifier": identifier
        }

    def fetch(self, node_identifier: str, structure_address: str) -> list[dict]:
        functions = self.mongo.find({"node_identifier": node_identifier, "structure_address": structure_address})
        return [self.to_dict(function) for function in functions]

    def get(self, node_identifier: str, structure_address, identifier: str) -> dict:
        function = self.mongo.find_one({
            "node_identifier": node_identifier,
            "structure_address": structure_address,
            "identifier": identifier
        })

        if not function:
            raise NotFoundException(f"Function {identifier} not found for structure {structure_address} on node {node_identifier}")

        return self.to_dict(function)

    def update(self, node_identifier: str, structure_address: str, identifier: str, display_name: str, description: str):
        fields = {
            "description": description,
            "updated_at": current_datetime()
        }

        if display_name:
            fields["display_name"] = display_name

        result = self.mongo.update_one({
            "node_identifier": node_identifier,
            "structure_address": structure_address,
            "identifier": identifier
        }, {"$set": fields})

        if result.matched_count == 0:
            raise NotFoundException(f"Function {identifier} not found for structure {structure_address} on node {node_identifier}")

        return {
            "identifier": identifier
        }

    def delete(self, node_identifier: str, structure_address: str,  identifier: str) -> dict:
        result = self.mongo.delete_one({
            "node_identifier": node_identifier,
            "structure_address": structure_address,
            "identifier": identifier
        })

        if result.deleted_count == 0:
            raise NotFoundException(f"Function {identifier} not found for structure {structure_address} on node {node_identifier}")

        return {
            "identifier": identifier
        }

    @staticmethod
    def to_dict(self):
        return {
            "node_identifier": self.get("node_identifier"),
            "structure_address": self.get("structure_address"),
            "identifier": self.get("identifier"),
            "display_name": self.get("display_name"),
            "description": self.get("description"),
            "input_json_schema": self.get("input_json_schema"),
            "output_json_schema": self.get("output_json_schema"),
            "creator": self.get("creator"),
            "created_at": self.get("created_at"),
            "updated_at": self.get("updated_at"),
        }