from service.config_service import ConfigService
from service.node_service import NodeService
from service.remote_node_service import RemoteNodeService
from utils.ed25519_utils import string_to_public_key


class NodeKeyService:
    def __init__(self, node_service: NodeService, remote_node_service: RemoteNodeService, config_service: ConfigService) -> None:
        self.node_service = node_service
        self.remote_node_service = remote_node_service
        self.config_service = config_service

    def get_signing_public_key(self, kid: str) -> (str, str, str):
        kid_components = kid.split("/")

        if len(kid_components) != 2:
            raise Exception("Invalid kid")

        vertex_endpoint = kid_components[0]
        node_identifier = kid_components[1]
        
        if self.config_service.get_vertex_endpoint() == vertex_endpoint:
            return vertex_endpoint, node_identifier, string_to_public_key(self.node_service.get(node_identifier).get("signing_public_key"))
        else:
            return vertex_endpoint, node_identifier, string_to_public_key(self.remote_node_service.get(vertex_endpoint, node_identifier).get("signing_public_key"))
