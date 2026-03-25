$.ajaxSetup({
    statusCode: {
        401: function(jqxhr, textStatus, errorThrown) {
            localStorage.clear()
            window.location.href = "/login"
        }
    }
})

function displayError(parent, message) {
    parent.html(`<div class="alert alert-danger">${message}</div>`);
}

function displaySuccess(parent, message) {
    parent.html(`<div class="alert alert-success">${message}</div>`);
}

function getHeaders() {
    return {
        Authorization: `Bearer ${localStorage.getItem('token')}`,
    }
}

function signUp(username, password, organizationName, billingEmail, success, error) {
    $.ajax({
        url: '/api/v1/users/signup',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            username,
            password,
            organization_name: organizationName,
            billing_email: billingEmail,
        }),
        success,
        error,
    })
}

function getToken(username, password, organizationName, success, error) {
    $.ajax({
        url: '/api/v1/users/token',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            username,
            password,
            organization_name: organizationName,
        }),
        success,
        error,
    })
}

function getOrganization(success, error) {
    $.ajax({
        url: '/api/v1/organization',
        method: 'GET',
        headers: getHeaders(),
        success,
        error,
    })
}

function updateOrganization(billingEmail, success, error) {
    $.ajax({
        url: '/api/v1/organization',
        method: 'PUT',
        headers: getHeaders(),
        contentType: 'application/json',
        data: JSON.stringify({
            billing_email: billingEmail,
        }),
        success,
        error,
    })
}
