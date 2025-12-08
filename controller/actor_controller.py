from flask import Flask

from service.actor_service import ActorService
from utils.api_utils import authenticate_actor, optional_param, required_param

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

        @self.app.get("/api/v1/actors/current")
        @authenticate_actor(must_be_local=True)
        def get_current_actor_details(actor):
            return self.actor_service.get(
                actor.get("audience_node_identifier"),
                actor.get("identifier")
            )

        @self.app.put("/api/v1/actors/current")
        @authenticate_actor(must_be_local=True)
        def update_current_actor(actor):
            return self.actor_service.update(
                actor.get("audience_node_identifier"),
                actor.get("identifier"),
                optional_param("type"),
                optional_param("display_name"),
                optional_param("description")
            )

        @self.app.put("/api/v1/actors/current/password")
        @authenticate_actor(must_be_local=True)
        def change_current_actor_password(actor):
            return self.actor_service.change_password(
                actor.get("audience_node_identifier"),
                actor.get("identifier"),
                required_param("password")
            )

        @self.app.delete("/api/v1/actors/current")
        @authenticate_actor(must_be_local=True)
        def delete_current_actor(actor):
            return self.actor_service.delete(
                actor.get("audience_node_identifier"),
                actor.get("identifier")
            )
