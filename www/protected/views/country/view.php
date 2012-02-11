<?php
$this->pageTitle = $model->name;

$this->breadcrumbs=array(
	'Страны'=>array('index'),
	$model->name,
);

$this->menu=array(
	array('label'=>'Все страны', 'url'=>array('index')),
	array('label'=>'Добавить', 'url'=>array('create'), 'visible' => Yii::app()->user->is_admin),
	array('label'=>'Изменить', 'url'=>array('update', 'id'=>$model->country_id), 'visible' => Yii::app()->user->is_admin),
	array('label'=>'Удалить', 'url'=>'#', 'linkOptions'=>array('submit'=>array('delete','id'=>$model->country_id),'confirm'=>'Вы уверены, что хотите удалить эту запись?'), 'visible' => Yii::app()->user->is_admin)
);
?>

<h1><?= $this->pageTitle ?></h1>

<?php

$this->widget('zii.widgets.CDetailView', array(
	'data'=>$model,
	'attributes'=>array(
        'name'
	),
));

if (Yii::app()->user->is_admin )
{
    ?><br/><h3>Добавление города</h3> <?
    echo $this->renderPartial('_cityform',array('model'=>$city));
}

    $data = new CActiveDataProvider('City',array('criteria'=>Array('condition'=>"`country_id`='$model->country_id'",'order'=>'name')));
    $this->widget('zii.widgets.grid.CGridView', array(  
	'id'=>'city-grid',
	'dataProvider'=>$data,
	'columns'=>array(
		'name',
		 array(     'class'=>'CButtonColumn',
                    'buttons'=>array(
                        'delete'=>array('url'=>'Yii::app()->createUrl("country/deleteCity",array("id"=>$data->city_id))'),
                        'update'=>array('url'=>'Yii::app()->createUrl("country/updateCity",array("id"=>$data->city_id))')
                                            
                    ),
                    'template'=>'{update}{delete}',
              )
	),
));
?>