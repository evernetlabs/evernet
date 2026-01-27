from pymongo.collection import Collection

from exception.types import NotFoundException
from service.structure_service import StructureService
from utils.time import current_datetime


class StateService:
    def __init__(self, mongo: Collection, structure_service: StructureService):
        self.mongo = mongo
        self.structure_service = structure_service

    def create(self, node_identifier: str, structure_address: str, identifier: str, display_name: str, description: str, creator: str):
        if not self.structure_service.exists(node_identifier, structure_address):
            raise NotFoundException(f"Structure {structure_address} not found on node {node_identifier}")

        if self.mongo.count_documents({
            "node_identifier": node_identifier,
            "structure_address": structure_address,
            "identifier": identifier
        }) > 0:
            raise Exception(f"State {identifier} already exists for structure {structure_address} on node {node_identifier}")

        self.mongo.insert_one({
            "node_identifier": node_identifier,
            "structure_address": structure_address,
            "identifier": identifier,
            "display_name": display_name,
            "description": description,
            "creator": creator,
            "created_at": current_datetime(),
            "updated_at": current_datetime()
        })

        return {
            "identifier": identifier,
        }

    def fetch(self, node_identifier: str, structure_address: str) -> list[dict]:
        states = self.mongo.find({
            "node_identifier": node_identifier,
            "structure_address": structure_address
        })

        return [self.to_dict(state) for state in states]

    def get(self, node_identifier: str, structure_address: str, identifier: str) -> dict:
        state = self.mongo.find_one({
            "node_identifier": node_identifier,
            "structure_address": structure_address,
            "identifier": identifier
        })

        if not state:
            raise NotFoundException(f"State {identifier} not found for structure {structure_address} on node {node_identifier}")

        return self.to_dict(state)

    def update(self, node_identifier: str, structure_address: str, identifier: str, display_name: str, description: str):
        fields = {
            "updated_at": current_datetime(),
            "description": description,
        }

        if display_name:
            fields["display_name"] = display_name

        result = self.mongo.update_one({
            "node_identifier": node_identifier,
            "structure_address": structure_address,
            "identifier": identifier
        }, {"$set": fields})

        if result.matched_count == 0:
            raise NotFoundException(f"State {identifier} not found for structure {structure_address} on node {node_identifier}")

        return {
            "identifier": identifier,
        }

    def delete(self, node_identifier: str, structure_address: str, identifier: str) -> dict:
        result = self.mongo.delete_one({
            "node_identifier": node_identifier,
            "structure_address": structure_address,
            "identifier": identifier
        })

        if result.deleted_count == 0:
            raise NotFoundException(f"State {identifier} not found for structure {structure_address} on node {node_identifier}")

        return {
            "identifier": identifier,
        }

    @staticmethod
    def to_dict(self):
        return {
            "node_identifier": self.get("node_identifier"),
            "structure_address": self.get("structure_address"),
            "identifier": self.get("identifier"),
            "display_name": self.get("display_name"),
            "description": self.get("description"),
            "creator": self.get("creator"),
            "created_at": self.get("created_at"),
            "updated_at": self.get("updated_at")
        }