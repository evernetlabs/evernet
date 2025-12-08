import requests


class RemoteNodeService:
    def __init__(self) -> None:
        pass

    def get(self, vertex_endpoint: str, node_identifier: str) -> dict:
        response = requests.get(f"{vertex_endpoint}/api/v1/nodes/{node_identifier}")
        response.raise_for_status()
        return response.json()
