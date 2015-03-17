/**
 * Created by pi1 on 17/03/2015.
 */
$(document).ready(function () {
    var tagQuery = $.ajax('http://localhost:8081/options/search/findByPerSampleTrue/');

    // compile template using Handlebars
    var selectTemplate = Handlebars.compile($('#select-template').html()),
        textTemplate = Handlebars.compile($('#text-template').html()),
        optionTemplate = Handlebars.compile($('#option-template').html());

    tagQuery.then(function (data, status) {
        console.log(data);
        console.log(status);

        var options = data._embedded.options;

        var $nameSelect = $('#tagName'),
            $valueWrapper = $('#value-wrapper');

        $.each(options, function (index, option) {
            option.index = index;
            $nameSelect.append(optionTemplate(option));
        });

        $nameSelect.on('change', function (e) {
            $valueWrapper.html('');

            var valueSelected = this.value;
            var selectedOption = options[$(this).find(":selected").data('index')];

            if (!selectedOption.restrictedOptions) {
                $valueWrapper.append(textTemplate(selectedOption));
            } else {
                console.log(selectedOption.restrictedOptions);
                console.log(stringByCommasToArray(selectedOption.restrictedOptions));
                var context = {
                    name: selectedOption.name,
                    options: stringByCommasToArray(selectedOption.restrictedOptions)
                };
                $valueWrapper.append(selectTemplate(context));
            }
        });

    }, function (a, b, c) {

    })
});