<?php

$this->pageTitle = 	$model->location . ' '.$model->cinema . ' зал '.$model->hall;

$this->breadcrumbs=array(
	'Проекторы'=>array('index'),
    $this->pageTitle    
);

$this->menu=array(
	array('label'=>'Все проекторы', 'url'=>array('index')),
	array('label'=>'Создать', 'url'=>array('create'), 'visible' => Yii::app()->user->is_admin),
//	array('label'=>'Добавить ролик', 'url'=>array('lnkSoasPromo/create', 'soas_id'=>$model->soas_id), 'visible' => Yii::app()->user->is_admin),
//	array('label'=>'Изменить', 'url'=>array('update', 'id'=>$model->soas_id), 'visible' => Yii::app()->user->is_admin),
//	array('label'=>'Удалить', 'url'=>'#', 'linkOptions'=>array('submit'=>array('delete','id'=>$model->soas_id),'confirm'=>'Вы уверены, что хотите удалить эту запись?'), 'visible' => Yii::app()->user->is_admin)
);
?>

<h1><?= $this->pageTitle ?></h1>

<?php $this->widget('zii.widgets.CDetailView', array(
	'data'=>$model,
	'attributes'=>array(
        'soas_id',
        'location',
        'cinema',
	    'hall',
		'annotation',
        'last_heart_beat',
        'registration_time'
	),
)); 
?><br /><br /><h3>История сеансов связи</h3><?
$ConnectionsDataProvider = new CActiveDataProvider('ConnectionLog', array(
	'criteria'=>array(
        'condition'=>'soas_id=:soas_id order by `time` desc',
        'params'=>array(':soas_id'=>$model->soas_id)
    )
	//,'pagination' => array ('pageSize'=>1)
));

$this->widget('zii.widgets.grid.CGridView', array(
	'id'=>'connectionslog-grid',
	'dataProvider'=>$ConnectionsDataProvider,
	'columns'=>array(
		'time','remote_addr'
	)
));
?>