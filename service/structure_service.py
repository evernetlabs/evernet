from pymongo.collection import Collection

from service.config_service import ConfigService
from service.node_service import NodeService
from util.time import current_datetime


class StructureService:
    def __init__(self, mongo: Collection, node_service: NodeService, config_service: ConfigService):
        self.mongo = mongo
        self.node_service = node_service
        self.config_service = config_service

    def register(
            self, node_identifier: str, identifier: str,
            version: str, display_name: str, description: str,
            properties: dict[str, dict],
            functions: dict[str, dict],
            events: dict[str, dict],
            states: dict[str, dict],
            relationships: dict[str, dict],
            creator: str
    ):
        if not self.node_service.exists(node_identifier):
            raise Exception(f"Node {node_identifier} not found")

        if self.mongo.count_documents({
            "node_identifier": node_identifier,
            "identifier": identifier,
            "version": version,
        }) > 0:
            raise Exception(f"Structure {identifier} with version {version} already exists on node {node_identifier}")

        self.mongo.insert_one({
            "node_identifier": node_identifier,
            "identifier": identifier,
            "version": version,
            "display_name": display_name,
            "description": description,
            "properties": self.__validate_properties(properties),
            "functions": self.__validate_functions(functions),
            "events": self.__validate_events(events),
            "states": self.__validate_states(states),
            "relationships": self.__validate_relationships(relationships),
            "creator": creator,
            "created_at": current_datetime(),
            "updated_at": current_datetime(),
        })

        return {
            "node_identifier": node_identifier,
            "identifier": identifier,
            "version": version
        }

    def fetch(self, node_identifier: str, page: int = 0, size: int = 50) -> list[dict]:
        structures = self.mongo.find({
            "node_identifier": node_identifier,
        }).skip(page * size).limit(size)

        return [self.to_dict(structure) for structure in structures]

    def fetch_versions(self, node_identifier: str, identifier: str, page: int = 0, size: int = 50) -> list[dict]:
        structures = self.mongo.find({
            "node_identifier": node_identifier,
            "identifier": identifier,
        }).skip(page * size).limit(size)

        return [self.to_dict(structure) for structure in structures]

    def get(self, node_identifier: str, identifier: str, version: str) -> dict:
        structure = self.mongo.find_one({
            "node_identifier": node_identifier,
            "identifier": identifier,
            "version": version
        })

        if not structure:
            raise Exception(f"Structure {identifier} with version {version} not found on node {node_identifier}")

        return self.to_dict(structure)

    @staticmethod
    def to_dict(self):
        return {
            "node_identifier": self["node_identifier"],
            "identifier": self["identifier"],
            "version": self["version"],
            "display_name": self["display_name"],
            "description": self["description"],
            "properties": self["properties"],
            "functions": self["functions"],
            "events": self["events"],
            "states": self["states"],
            "relationships": self["relationships"],
            "creator": self["creator"],
            "created_at": self["created_at"],
            "updated_at": self["updated_at"]
        }

    @staticmethod
    def __validate_properties(properties: dict[str, dict]) -> dict[str, dict]:
        validated_properties = {}
        if not properties or len(properties) == 0:
            return validated_properties
        for key, value in properties.items():
            if not isinstance(value, dict):
                raise Exception(f"Property {key} data is not valid")
            if "display_name" not in value:
                raise Exception(f"Property {key} data does not contain display name")
            if "description" not in value:
                raise Exception(f"Property {key} data does not contain description")
            if "data_schema" not in value:
                raise Exception(f"Property {key} data does not contain data schema")
            validated_properties[key] = {
                "display_name": value["display_name"],
                "description": value["description"],
                "data_schema": value["data_schema"],
            }
        return validated_properties

    @staticmethod
    def __validate_functions(functions: dict[str, dict]) -> dict[str, dict]:
        validated_functions = {}
        if not functions or len(functions) == 0:
            return validated_functions
        for key, value in functions.items():
            if not isinstance(value, dict):
                raise Exception(f"Function {key} data is not valid")
            if "display_name" not in value:
                raise Exception(f"Function {key} data does not contain display name")
            if "description" not in value:
                raise Exception(f"Function {key} data does not contain description")
            if "input_schema" not in value:
                raise Exception(f"Function {key} data does not contain data schema")
            if "output_schema" not in value:
                raise Exception(f"Function {key} data does not contain data schema")
            validated_functions[key] = {
                "display_name": value["display_name"],
                "description": value["description"],
                "input_schema": value["input_schema"],
                "output_schema": value["output_schema"],
            }
        return validated_functions

    @staticmethod
    def __validate_events(events: dict[str, dict]) -> dict[str, dict]:
        validated_events = {}
        if not events or len(events) == 0:
            return validated_events
        for key, value in events.items():
            if not isinstance(value, dict):
                raise Exception(f"Event {key} data is not valid")
            if "display_name" not in value:
                raise Exception(f"Event {key} data does not contain display name")
            if "description" not in value:
                raise Exception(f"Event {key} data does not contain description")
            if "data_schema" not in value:
                raise Exception(f"Event {key} data does not contain data schema")
            validated_events[key] = {
                "display_name": value["display_name"],
                "description": value["description"],
                "data_schema": value["data_schema"],
            }

        return validated_events

    @staticmethod
    def __validate_states(states: dict[str, dict]) -> dict[str, dict]:
        validated_states = {}
        if not states or len(states) == 0:
            return validated_states
        for key, value in states.items():
            if not isinstance(value, dict):
                raise Exception(f"State {key} data is not valid")
            if "display_name" not in value:
                raise Exception(f"State {key} data does not contain display name")
            if "description" not in value:
                raise Exception(f"State {key} data does not contain description")
            validated_states[key] = {
                "display_name": value["display_name"],
                "description": value["description"],
            }
        return validated_states

    @staticmethod
    def __validate_relationships(relationships) -> dict[str, dict]:
        validated_relationships = {}
        if not relationships or len(relationships) == 0:
            return validated_relationships
        for key, value in relationships.items():
            if not isinstance(value, dict):
                raise Exception(f"Relationship {key} data is not valid")
            if "display_name" not in value:
                raise Exception(f"Relationship {key} data does not contain display name")
            if "description" not in value:
                raise Exception(f"Relationship {key} data does not contain description")
            if "type" not in value:
                raise Exception(f"Relationship {key} data does not contain type")
            if value["type"] not in ["one", "many"]:
                raise Exception(f"Relationship {key} data does not contain correct type value")
            if "target_structure_address" not in value:
                raise Exception(f"Relationship {key} data does not contain target structure address")
            validated_relationships[key] = {
                "display_name": value["display_name"],
                "description": value["description"],
                "target_structure_address": value["target_structure_address"],
                "type": value["type"],
            }
        return validated_relationships
