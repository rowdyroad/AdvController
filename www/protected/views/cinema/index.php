<?php
$this->pageTitle = 'Кинотеатры';

$this->breadcrumbs=array(
	$this->pageTitle
);

$this->menu=array(
	array('label'=>'Создать', 'url'=>array('create'), 'visible' => Yii::app()->user->is_admin)
);

Yii::app()->clientScript->registerScript('search', "
$('.search-button').click(function(){
	$('.search-form').toggle();
	return false;
});
$('.search-form form').submit(function(){
	$.fn.yiiGridView.update('cinema-grid', {
		data: $(this).serialize()
	});
	return false;
});
");
?>

<h1><?= $this->pageTitle ?></h1>

<p>Вы можете использовать операторы сравнения (<b>&lt;</b>, <b>&lt;=</b>, <b>&gt;</b>, <b>&gt;=</b>, <b>&lt;&gt;</b>
или <b>=</b>) в начале каждого критерия поиска.</p>

<?php echo CHtml::link('Расширенный поиск','#',array('class'=>'search-button')); ?>
<div class="search-form" style="display:none">
<?php $this->renderPartial('_search',array(
	'model'=>$model,
)); ?>
</div><!-- search-form -->

<?php $this->widget('zii.widgets.grid.CGridView', array(
	'id'=>'cinema-grid',
	'dataProvider'=>$model->search(),
	'filter'=>$model,
	'columns'=>array(
		'name',
		'comments',
		array(
			'class'=>'CButtonColumn',
		),
	),
)); ?>
