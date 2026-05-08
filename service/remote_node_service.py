import requests
from service.config_service import ConfigService


class RemoteNodeService:

    def __init__(self, config_service: ConfigService):
        self.config_service = config_service

    def get(self, vertex_endpoint: str, identifier: str):
        protocol = self.config_service.get_federation_protocol()
        response = requests.get(f"{protocol}://{vertex_endpoint}/api/v1/nodes/{identifier}")
        response.raise_for_status()
        return response.json()