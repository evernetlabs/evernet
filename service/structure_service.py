from pymongo.collection import Collection

from service.config_service import ConfigService
from service.node_service import NodeService
from utils.time_utils import current_datetime

class StructureService:
    def __init__(self, mongo: Collection, node_service: NodeService, config_service: ConfigService) -> None:
        self.mongo = mongo
        self.node_service = node_service
        self.config_service = config_service

    def create(self, node_identifier: str, identifier: str, display_name: str, description: str, creator: str) -> dict:
        if not self.node_service.exists(node_identifier):
            raise Exception(f"Node {node_identifier} not found")

        vertex_endpoint = self.config_service.get_vertex_endpoint()
        structure_address = f"{vertex_endpoint}/{node_identifier}/structures/{identifier}"

        if self.mongo.count_documents({
            'address': structure_address,
            'node_identifier': node_identifier
        }) > 0:
            raise Exception(f"Structure {structure_address} already exists on node {node_identifier}")
        
        self.mongo.insert_one({
            'node_identifier': node_identifier,
            'address': structure_address,
            'display_name': display_name,
            'description': description,
            'creator': creator,
            'created_at': current_datetime(),
            'updated_at': current_datetime()
        })

        return {
            "address": structure_address
        }

    def fetch(self, node_identifier: str, page: int, size: int) -> list[dict]:
        structures = self.mongo.find({'node_identifier': node_identifier}).skip(page * size).limit(size)
        return [self.to_dict(structure) for structure in structures]

    @staticmethod
    def to_dict(structure: dict) -> dict:
        return {
            "node_identifier": structure.get("node_identifier"),
            "address": structure.get("address"),
            "display_name": structure.get("display_name"),
            "description": structure.get("description"),
            "creator": structure.get("creator"),
            "created_at": structure.get("created_at"),
            "updated_at": structure.get("updated_at")
        }
