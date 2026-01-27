import requests

from service.config_service import ConfigService


class RemoteNodeService:
    def __init__(self, config_service: ConfigService) -> None:
        self.config_service = config_service

    def get(self, vertex_endpoint: str, node_identifier: str) -> dict:
        resp = requests.get(f"{self.config_service.get_federation_protocol()}://{vertex_endpoint}/api/v1/nodes/{node_identifier}")
        resp.raise_for_status()
        return resp.json()
