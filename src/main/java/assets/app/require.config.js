var require = {
    baseUrl: "/assets",
    paths: {
        "crossroads": "js/crossroads",
        "jquery": "js/jquery",
        "jquery-migrate": "js/jquery-migrate",
        "knockout": "js/knockout",
        "knockout-projections": "js/knockout-projections",
        "signals": "js/signals",
        "hasher": "js/hasher",
        "text": "js/requirejs-text",
        "jquery-ui": "js/jquery-ui",
        "knockout-jqueryui": "js/knockout-jqueryui",
        "dash-repo": "js/dash-repo",
        "router": "js/router"
    },
    shim: {
        "jquery-migrate": {
            deps: ["jquery"],
            exports: "jquery"
        }
    }
};
