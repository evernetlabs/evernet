from typing import Tuple

from cryptography.hazmat.primitives.asymmetric.ed25519 import Ed25519PublicKey

from service.config_service import ConfigService
from service.remote_node_service import RemoteNodeService
from service.node_service import NodeService
from utils.ed25519 import string_to_public_key


class NodeKeyService:
    def __init__(self, node_service: NodeService, remote_node_service: RemoteNodeService, config_service: ConfigService) -> None:
        self.node_service = node_service
        self.remote_node_service = remote_node_service
        self.config_service = config_service

    def get_signing_public_key(self, kid: str) -> Tuple[str, str, Ed25519PublicKey]:
        kid_component = kid.split("/")
        if len(kid_component) != 2:
            raise ValueError("Invalid KID format")
        node_identifier = kid_component[1]
        vertex_endpoint = kid_component[0]

        if self.config_service.get_vertex_endpoint() == vertex_endpoint:
            signing_public_key = self.node_service.get(node_identifier).get("signing_public_key")
        else:
            signing_public_key = self.remote_node_service.get(vertex_endpoint, node_identifier).get("signing_public_key")

        return vertex_endpoint, node_identifier, string_to_public_key(signing_public_key)