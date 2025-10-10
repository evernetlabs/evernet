import requests

from config.config_service import ConfigService


class RemoteNodeService:
    def __init__(self, config_service: ConfigService):
        self.config_service = config_service

    def get(self, vertex_endpoint: str, identifier: str):
        resp = requests.get(f"{self.config_service.get_federation_protocol()}://{vertex_endpoint}/api/v1/nodes/{identifier}")
        resp.raise_for_status()
        return resp.json()
