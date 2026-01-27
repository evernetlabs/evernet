from pymongo.collection import Collection

from exception.types import NotFoundException
from service.node_service import NodeService
from utils.time import current_datetime


class StructureService:
    def __init__(self, mongo: Collection, node_service: NodeService):
        self.mongo = mongo
        self.node_service = node_service

    def create(self, node_identifier: str, identifier: str, display_name: str, description: str, creator: str) -> dict:
        if not self.node_service.exists(node_identifier):
            raise NotFoundException(f"Node {node_identifier} not found")

        if self.mongo.count_documents({
            "node_identifier": node_identifier,
            "identifier": identifier
        }) > 0:
            raise Exception(f"Structure {identifier} already exists on node {node_identifier}")

        self.mongo.insert_one({
            "node_identifier": node_identifier,
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

    def fetch(self, node_identifier: str, page: int = 0, size: int = 50) -> list[dict]:
        structures = self.mongo.find({"node_identifier": node_identifier}).skip(page * size).limit(size)
        return [self.to_dict(structure) for structure in structures]

    def get(self, node_identifier: str, identifier: str) -> dict:
        structure = self.mongo.find_one({
            "node_identifier": node_identifier,
            "identifier": identifier
        })

        if not structure:
            raise NotFoundException(f"Structure {identifier} not found on node {node_identifier}")

        return self.to_dict(structure)

    def update(self, node_identifier: str, identifier: str, display_name: str, description: str) -> dict:
        fields = {
            "updated_at": current_datetime()
        }

        if display_name:
            fields["display_name"] = display_name

        fields["description"] = description

        result = self.mongo.update_one({
            "node_identifier": node_identifier,
            "identifier": identifier
        }, {
            "$set": fields
        })

        if result.matched_count == 0:
            raise NotFoundException(f"Structure {identifier} not found on node {node_identifier}")

        return {
            "identifier": identifier
        }

    def delete(self, node_identifier: str, identifier: str) -> dict:
        result = self.mongo.delete_one({
            "node_identifier": node_identifier,
            "identifier": identifier
        })

        if result.deleted_count == 0:
            raise NotFoundException(f"Structure {identifier} not found on node {node_identifier}")

        return {
            "identifier": identifier
        }

    @staticmethod
    def to_dict(self):
        return {
            "id": str(self.get("_id")),
            "node_identifier": self.get("node_identifier"),
            "identifier": self.get("identifier"),
            "display_name": self.get("display_name"),
            "description": self.get("description"),
            "creator": self.get("creator"),
            "created_at": self.get("created_at"),
            "updated_at": self.get("updated_at"),
        }
