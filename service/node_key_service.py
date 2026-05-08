from service.config_service import ConfigService
from service.node_service import NodeService
from service.remote_node_service import RemoteNodeService
from util.key import ed25519_string_to_public_key


class NodeKeyService:
    def __init__(self, config_service: ConfigService, node_service: NodeService, remote_node_service: RemoteNodeService):
        self.config_service = config_service
        self.node_service = node_service
        self.remote_node_service = remote_node_service

    def get_signing_public_key(self, kid: str):
        kid_components = kid.split("/")

        if len(kid_components) != 2:
            raise Exception("Invalid kid in jwt token")

        current_vertex_endpoint = self.config_service.get_vertex_endpoint()
        kid_vertex_endpoint = kid_components[0]
        kid_node_identifier = kid_components[1]

        if kid_vertex_endpoint == current_vertex_endpoint:
            node = self.node_service.get(kid_node_identifier)
            return kid_vertex_endpoint, kid_node_identifier, ed25519_string_to_public_key(node["signing_public_key"])
        else:
            node = self.remote_node_service.get(kid_vertex_endpoint, kid_node_identifier)
            return kid_vertex_endpoint, kid_node_identifier, ed25519_string_to_public_key(node["signing_public_key"])
