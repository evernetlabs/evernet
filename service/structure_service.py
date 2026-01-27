from pymongo.collection import Collection

from exception.types import NotFoundException
from service.config_service import ConfigService
from service.node_service import NodeService
from utils.time import current_datetime


class StructureService:
    def __init__(self, mongo: Collection, node_service: NodeService, config_service: ConfigService):
        self.mongo = mongo
        self.node_service = node_service
        self.config_service = config_service

    def cache(self, node_identifier: str, address: str, display_name: str, description: str, creator: str) -> dict:
        self.mongo.insert_one({
            "node_identifier": node_identifier,
            "address": address,
            "display_name": display_name,
            "description": description,
            "creator": creator,
            "created_at": current_datetime(),
            "updated_at": current_datetime()
        })

        return {
            "address": address,
        }

    def create(self, node_identifier: str, identifier: str, display_name: str, description: str, creator: str) -> dict:
        if not self.node_service.exists(node_identifier):
            raise NotFoundException(f"Node {node_identifier} not found")

        vertex_endpoint = self.config_service.get_vertex_endpoint()
        address  = f"{vertex_endpoint}/{node_identifier}/structures/{identifier}"

        if self.mongo.count_documents({
            "node_identifier": node_identifier,
            "address": address
        }) > 0:
            raise Exception(f"Structure {address} already exists on node {node_identifier}")

        self.mongo.insert_one({
            "node_identifier": node_identifier,
            "address": address,
            "display_name": display_name,
            "description": description,
            "creator": creator,
            "created_at": current_datetime(),
            "updated_at": current_datetime()
        })

        return {
            "address": address,
        }

    def fetch(self, node_identifier: str, page: int = 0, size: int = 50) -> list[dict]:
        structures = self.mongo.find({"node_identifier": node_identifier}).skip(page * size).limit(size)
        return [self.to_dict(structure) for structure in structures]

    def get(self, node_identifier: str, address: str) -> dict:
        structure = self.mongo.find_one({
            "node_identifier": node_identifier,
            "address": address
        })

        if not structure:
            raise NotFoundException(f"Structure {address} not found on node {node_identifier}")

        return self.to_dict(structure)

    def get_without_exception(self, node_identifier: str, address: str) -> dict | None:
        structure = self.mongo.find_one({
            "node_identifier": node_identifier,
            "address": address
        })

        if not structure:
            return None

        return self.to_dict(structure)

    def update(self, node_identifier: str, address: str, display_name: str, description: str) -> dict:
        fields = {
            "updated_at": current_datetime()
        }

        if display_name:
            fields["display_name"] = display_name

        fields["description"] = description

        result = self.mongo.update_one({
            "node_identifier": node_identifier,
            "address": address
        }, {
            "$set": fields
        })

        if result.matched_count == 0:
            raise NotFoundException(f"Structure {address} not found on node {node_identifier}")

        return {
            "address": address
        }

    def delete(self, node_identifier: str, address: str) -> dict:
        result = self.mongo.delete_one({
            "node_identifier": node_identifier,
            "address": address
        })

        if result.deleted_count == 0:
            raise NotFoundException(f"Structure {address} not found on node {node_identifier}")

        return {
            "address": address
        }

    def exists(self, node_identifier: str, address: str) -> bool:
        return self.mongo.count_documents({
            "node_identifier": node_identifier,
            "address": address
        }) > 0

    @staticmethod
    def to_dict(self):
        return {
            "id": str(self.get("_id")),
            "address": self.get("address"),
            "identifier": self.get("identifier"),
            "display_name": self.get("display_name"),
            "description": self.get("description"),
            "creator": self.get("creator"),
            "created_at": self.get("created_at"),
            "updated_at": self.get("updated_at"),
        }
