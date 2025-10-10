from cryptography.hazmat.primitives.asymmetric.ed25519 import Ed25519PublicKey

from config.config_service import ConfigService
from node.node_service import NodeService
from node.remote_node_service import RemoteNodeService
from utils.ed25519_utils import string_to_public_key


class NodeKeyService:
    def __init__(self, node_service: NodeService, remote_node_service: RemoteNodeService, config_service: ConfigService):
        self.node_service = node_service
        self.remote_node_service = remote_node_service
        self.config_service = config_service

    def get_signing_public_key(self, key_id: str) -> tuple[str, str, Ed25519PublicKey]:
        key_id_components = key_id.split("/")
        if len(key_id_components) != 2:
            raise Exception(f"Invalid key id {key_id}")

        vertex_endpoint = key_id_components[0]
        node_identifier = key_id_components[1]

        if self.config_service.get_vertex_endpoint() == vertex_endpoint:
            node = self.node_service.get(node_identifier)
        else:
            node = self.remote_node_service.get(vertex_endpoint, node_identifier)

        if not node:
            raise Exception(f'Node {node_identifier} not found on vertex {vertex_endpoint}')

        return vertex_endpoint, node_identifier, string_to_public_key(node.get('signing_public_key'))
