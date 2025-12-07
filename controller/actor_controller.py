from flask import Flask

from service.actor_service import ActorService
from utils.api_utils import optional_param, required_param

class ActorController:
    def __init__(self, app: Flask, actor_service: ActorService) -> None:
        self.app = app
        self.actor_service = actor_service

    def register(self):
        
        @self.app.post("/api/v1/nodes/<node_identifier>/actors/signup")
        def actor_sign_up(node_identifier):
            return self.actor_service.sign_up(
                node_identifier,
                required_param("identifier"),
                required_param("password"),
                required_param("type"),
                required_param("display_name"),
                required_param("description")
            )

        @self.app.post("/api/v1/nodes/<node_identifier>/actors/token")
        def get_actor_token(node_identifier):
            return self.actor_service.get_token(
                node_identifier,
                required_param("identifier"),
                required_param("password"),
                optional_param("audience_node_address")
            )
