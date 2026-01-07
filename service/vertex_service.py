from service.config_service import ConfigService


class VertexService:
    def __init__(self, config_service: ConfigService):
        self.config_service = config_service

    def get(self):
        return {
            "endpoint": self.config_service.get_vertex_endpoint(),
            "display_name": self.config_service.get_vertex_display_name(),
            "description": self.config_service.get_vertex_description(),
            "federation_protocol": self.config_service.get_federation_protocol()
        }
