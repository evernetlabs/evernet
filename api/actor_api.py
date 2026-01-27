from flask import Flask

from service.actor_service import ActorService
from utils.api import required_param, optional_param, pagination_page, pagination_size
from utils.auth import authenticate_actor, authenticate_admin


class ActorAPI:
    def __init__(self, app: Flask, actor_service: ActorService) -> None:
        self.app = app
        self.actor_service = actor_service

    def register(self):

        @self.app.post('/api/v1/nodes/<node_identifier>/actors/signup')
        def sign_up_actor(node_identifier):
            return self.actor_service.sign_up(
                node_identifier,
                required_param("identifier"),
                required_param("password"),
                required_param("display_name"),
                optional_param("description"),
                required_param("type")
            )

        @self.app.post('/api/v1/nodes/<node_identifier>/actors/token')
        def get_actor_token(node_identifier):
            return self.actor_service.get_token(
                node_identifier,
                required_param("identifier"),
                required_param("password"),
                optional_param("audience_node_address")
            )

        @self.app.get('/api/v1/actors/current')
        @authenticate_actor(must_be_local=True)
        def get_actor(actor):
            return self.actor_service.get(actor.get("issuer_node_identifier"), actor.get("identifier"))

        @self.app.put('/api/v1/actors/current')
        @authenticate_actor(must_be_local=True)
        def update_actor(actor):
            return self.actor_service.update(
                actor.get("issuer_node_identifier"),
                actor.get("identifier"),
                optional_param("display_name"),
                optional_param("description"),
                optional_param("type"),
            )

        @self.app.put('/api/v1/actors/current/password')
        @authenticate_actor(must_be_local=True)
        def change_actor_password(actor):
            return self.actor_service.change_password(
                actor.get("issuer_node_identifier"),
                actor.get("identifier"),
                required_param("password"),
            )

        @self.app.delete('/api/v1/actors/current')
        @authenticate_actor(must_be_local=True)
        def delete_actor(actor):
            return self.actor_service.delete(
                actor.get("issuer_node_identifier"),
                actor.get("identifier"),
            )

        @self.app.post('/api/v1/admins/nodes/<node_identifier>/actors')
        @authenticate_admin
        def admin_api_add_actor(admin, node_identifier):
            return self.actor_service.add(
                node_identifier,
                required_param("identifier"),
                required_param("display_name"),
                optional_param("description"),
                required_param("type"),
                admin.get("identifier")
            )

        @self.app.get('/api/v1/admins/nodes/<node_identifier>/actors')
        @authenticate_admin
        def admin_api_fetch_actors(_, node_identifier):
            return self.actor_service.fetch(
                node_identifier,
                pagination_page(),
                pagination_size()
            )

        @self.app.get('/api/v1/admins/nodes/<node_identifier>/actors/<actor_identifier>')
        @authenticate_admin
        def admin_api_get_actor(_, node_identifier, actor_identifier):
            return self.actor_service.get(
                node_identifier,
                actor_identifier
            )

        @self.app.put('/api/v1/admins/nodes/<node_identifier>/actors/<actor_identifier>')
        @authenticate_admin
        def admin_api_update_actor(_, node_identifier, actor_identifier):
            return self.actor_service.update(
                node_identifier,
                actor_identifier,
                optional_param("display_name"),
                optional_param("description"),
                optional_param("type"),
            )

        @self.app.put('/api/v1/admins/nodes/<node_identifier>/actors/<actor_identifier>/password')
        @authenticate_admin
        def admin_api_reset_actor_password(_, node_identifier, actor_identifier):
            return self.actor_service.reset_password(
                node_identifier,
                actor_identifier
            )

        @self.app.delete('/api/v1/admins/nodes/<node_identifier>/actors/<actor_identifier>')
        @authenticate_admin
        def admin_api_delete_actor(_, node_identifier, actor_identifier):
            return self.actor_service.delete(
                node_identifier,
                actor_identifier,
            )
