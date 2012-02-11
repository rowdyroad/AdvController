<?php

$this->pageTitle = 'Журнал регистрации роликов';

$this->breadcrumbs=array(
    'Журнал регистрации роликов'
);
/*
Yii::app()->clientScript->registerCoreScript('jquery');
Yii::app()->clientScript->registerScriptFile('http://yandex.st/jquery-ui/1.8.2/jquery-ui.min.js');
Yii::app()->clientScript->registerCssFile('http://yandex.st/jquery-ui/1.8.0/themes/humanity/jquery.ui.all.min.css');
Yii::app()->clientScript->registerCss('auto_combo_css', '.ui-button { margin-left: -1px; }
	.ui-button-icon-only .ui-button-text { padding: 0.35em; } 
	.ui-autocomplete-input { margin: 0; padding: 0.48em 0 0.47em 0.45em; width: 30em; }
	.ui-autocomplete {
		max-height: 300px;
		overflow-y: auto;
	}
	.ui-autocomplete .ui-menu-item { font-size: 0.8em !important; }
	.ui-widget { font-size: 1em !important; }
	.ui-datepicker-title { font-size: 0.8em !important; }');
Yii::app()->clientScript->registerScript('auto_combo', '
	(function( $, undefined ) {
		$.widget( "ui.combobox", {
			options: {
				input_id: null,
				on_option_select: null
			},
			_create: function() {
				var self = this,
					select = this.element.hide(),
					selected = select.children( ":selected" ),
					value = selected.val() ? selected.text() : "";
				var input = $( "<input"+(self.options.input_id ? " id=\""+self.options.input_id+"\"" : "")+">" )
					.insertAfter( select )
					.val( value )
					.autocomplete({
						delay: 0,
						minLength: 0,
						source: function( request, response ) {
							var matcher = new RegExp( $.ui.autocomplete.escapeRegex(request.term), "i" );
							response( select.children( "option" ).map(function() {
								var text = $( this ).text();
								if ( this.value && ( !request.term || matcher.test(text) ) )
									return {
										label: text=="" ? "&mdash;" : text.replace(
											new RegExp(
												"(?![^&;]+;)(?!<[^<>]*)(" +
												$.ui.autocomplete.escapeRegex(request.term) +
												")(?![^<>]*>)(?![^&;]+;)", "gi"
											), "<strong>$1</strong>" ),
										value: text,
										option: this
									};
							}) );
						},
						select: function( event, ui ) {
							ui.item.option.selected = true;
							self._trigger( "selected", event, {
								item: ui.item.option
							});
							if (self.options.on_option_select)
							{
								self.options.on_option_select(select, ui.item.option);
							}
						},
						change: function( event, ui ) {
							if ( !ui.item ) {
								var matcher = new RegExp( "^" + $.ui.autocomplete.escapeRegex( $(this).val() ) + "$", "i" ),
									valid = false;
								select.children( "option" ).each(function() {
									if ( this.value.match( matcher ) ) {
										this.selected = valid = true;
										return false;
									}
								});
								if ( !valid ) {
									// remove invalid value, as it didnt match anything
									$( this ).val( "" );
									select.val( "" );
									return false;
								}
							}
						}
					})
					.addClass( "ui-widget ui-widget-content ui-corner-left" );

				input.data( "autocomplete" )._renderItem = function( ul, item ) {
					return $( "<li></li>" )
						.data( "item.autocomplete", item )
						.append( "<a>" + item.label + "</a>" )
						.appendTo( ul );
				};

				$( "<button type=\"button\">&nbsp;</button>" )
					.attr( "tabIndex", -1 )
					.attr( "title", "Развернуть" )
					.insertAfter( input )
					.button({
						icons: {
							primary: "ui-icon-triangle-1-s"
						},
						text: false
					})
					.removeClass( "ui-corner-all" )
					.addClass( "ui-corner-right ui-button-icon" )
					.click(function() {
						// close if already visible
						if ( input.autocomplete( "widget" ).is( ":visible" ) ) {
							input.autocomplete( "close" );
							return;
						}

						// pass empty string as value to search for, displaying all results
						input.autocomplete( "search", "" );
						input.focus();
					});
			}
		});
	})( jQuery );
	
	$( "#PromoRegistrationEvent_promo_id" ).combobox({ input_id: "autoC" });
');
/*
Yii::app()->clientScript->registerScript('search', "
$('.search-button').click(function(){
    $('.search-form').toggle();
    return false;
});
$('.search-form form').submit(function(){
    $.fn.yiiGridView.update('promo-registration-event-grid', {
        data: $(this).serialize()
    });
    return false;
});
");*/
?>

<h1><?= $this->pageTitle ?></h1>

<p>Вы можете использовать операторы сравнения (<b>&lt;</b>, <b>&lt;=</b>, <b>&gt;</b>, <b>&gt;=</b>, <b>&lt;&gt;</b>
или <b>=</b>) в начале каждого критерия поиска.</p>

<?php // echo CHtml::link('Расширенный поиск','#',array('class'=>'search-button')); ?>
<div class="search-form" style="display:block">
<?php $this->renderPartial('_search',array(
    'model'=>$model,
)); ?>
</div><!-- search-form -->

<?php 

$dp = new PromoRegistrationEventListProvider(array(
	'filterModel' => $model,
	'pagination' => array(
		'pageSize'=> 100
	)
));

$this->widget('zii.widgets.grid.CGridView', array(
    'id'=>'promo-registration-event-grid',
    'dataProvider'=>$dp,
	'columns' => array(
		array(
			'header' => 'Дата',
			'name' => 'dt_registered',
		),
		array(
			'header' => 'Страна/Город',
			'name' => 'location',
		),
		array(
			'header' => 'Кинотеатр',
			'name' => 'cinema_name',
		),
		array(
			'header' => '№ Зала',
			'name' => 'hall',
		),        
		array(
			'header' => 'Ролик',
			'name' => 'promo_name',
		),
	),
));




?>