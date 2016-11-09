define(["knockout", "jquery", "text!components/datasource/add.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;
        self.username = ko.observable();
        self.password = ko.observable();
        self.url = ko.observable();
        self.port = ko.observable();
        self.label = ko.observable();

        self.status = ko.observable("");
        self.messages = ko.observableArray([]);

        var isUpdate = params.datasourceId !== undefined;

        if (isUpdate) {
            self.actionLabel = ko.observable(ko.i18n('update'));
            self.title = ko.observable(ko.i18n('datasource.update.title'))
        } else {
            self.actionLabel = ko.observable(ko.i18n('create'));
            self.title = ko.observable(ko.i18n('datasource.add.title'))
        }

        if (isUpdate) {
            $.ajax("/api/datasource/" + params.datasourceId, {
                type: "GET",
                contentType: "application/json",
                success: function(datasource) {
                    self.username(datasource.username);
                    self.password(datasource.password);
                    self.url(datasource.url);
                    self.port(datasource.port);
                    self.label(datasource.label);
                    self.type(datasource.type);
                }
            });
        }

        var validate = $('form').validate({
            debug: true,
            messages: {
                username: {
                    required: ko.i18n("username.validation"),
                    minlength: ko.i18n("username.validation")
                },
                port: {
                    required: ko.i18n("port.validation"),
                    minlength: ko.i18n("port.validation"),
                    min: ko.i18n("port.validation")
                },
                url: {
                    required: ko.i18n("url.validation"),
                    minlength: ko.i18n("url.validation")
                },
                label: {
                    required: ko.i18n("label.validation"),
                    minlength: ko.i18n("label.validation")
                }
            }
        });

        self.submit = function(formElem) {
            if ($(formElem).valid()) {
                var datasource = {
                    url: self.url,
                    port: self.port,
                    username: self.username,
                    password: self.password,
                    label: self.label,
                    type: "MYSQL"
                };

                if (isUpdate) {
                    datasource.id = params.datasourceId;
                }

                $.ajax("/api/datasource/add-datasource", {
                    data: ko.toJSON(datasource),
                    type: "post", contentType: "application/json",
                    success: function(result) {
                        self.status(result.status);

                        var messages = result.messages;
                        var fieldMessages = result.fieldMessages;

                        self.messages([]);
                        if (messages != null) {
                            ko.utils.arrayPushAll(self.messages, result.messages)
                        }

                        if (fieldMessages != null) {
                            ko.utils.arrayForEach(["url", "username", "label"], function(elem){
                                if (elem in fieldMessages) {
                                    ko.utils.arrayPushAll(self.messages, fieldMessages[elem])
                                }
                            });
                        }
                    }
                });
            }
        };

        return self;
    }
    return { viewModel: viewModel, template: template };
});
