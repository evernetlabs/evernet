from pymongo.collection import Collection

from exception.types import NotFoundException, ClientException
from service.structure_clone_service import StructureCloneService
from service.structure_service import StructureService
from utils.time import current_datetime


class RelationshipService:
    def __init__(self, mongo: Collection, structure_service: StructureService, structure_clone_service: StructureCloneService):
        self.mongo = mongo
        self.structure_service = structure_service
        self.structure_clone_service = structure_clone_service

    def create(self, node_identifier: str, structure_address: str, target_structure_address: str, relationship_type: str, identifier: str, display_name: str, description: str, creator: str) -> dict:
        if relationship_type not in ["single", "multiple"]:
            raise ClientException(f"Invalid relationship type {relationship_type}")

        if not self.structure_service.exists(node_identifier, structure_address):
            raise NotFoundException(f"Structure {structure_address} not found on node {node_identifier}")

        target_structure = self.structure_clone_service.get_or_clone(target_structure_address, node_identifier)

        if self.mongo.count_documents({
            "node_identifier": node_identifier,
            "structure_address": structure_address,
            "identifier": identifier
        }) > 0:
            raise ClientException(f"Relationship {identifier} already exists for structure {structure_address} on node {node_identifier}")

        self.mongo.insert_one({
            "node_identifier": node_identifier,
            "structure_address": structure_address,
            "target_structure_address": target_structure['address'],
            "type": relationship_type,
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
        relationships = self.mongo.find({
            "node_identifier": node_identifier,
            "structure_address": structure_address
        })

        return [self.to_dict(relationship) for relationship in relationships]

    def get(self, node_identifier: str, structure_address: str, identifier: str) -> dict:
        relationship = self.mongo.find_one({
            "node_identifier": node_identifier,
            "structure_address": structure_address,
            "identifier": identifier
        })

        if not relationship:
            raise NotFoundException(f"Relationship {identifier} not found for structure {structure_address} on node {node_identifier}")

        return self.to_dict(relationship)

    def update(self, node_identifier: str, structure_address: str, identifier: str, display_name: str, description: str) -> dict:
        fields = {
            "updated_at": current_datetime(),
        }

        if display_name:
            fields["display_name"] = display_name

        fields["description"] = description

        result = self.mongo.update_one({
            "node_identifier": node_identifier,
            "structure_address": structure_address,
            "identifier": identifier
        }, {
            "$set": fields
        })

        if result.matched_count == 0:
            raise NotFoundException(f"Relationship {identifier} not found for structure {structure_address} on node {node_identifier}")

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
            raise NotFoundException(f"Relationship {identifier} not found for structure {structure_address} on node {node_identifier}")

        return {
            "identifier": identifier,
        }

    @staticmethod
    def to_dict(self):
        return {
            "structure_address": self.get("structure_address"),
            "target_structure_address": self.get("target_structure_address"),
            "type": self.get("type"),
            "identifier": self.get("identifier"),
            "display_name": self.get("display_name"),
            "description": self.get("description"),
            "creator": self.get("creator"),
            "created_at": self.get("created_at"),
            "updated_at": self.get("updated_at"),
        }