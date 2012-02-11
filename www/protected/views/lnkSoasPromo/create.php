<?php

switch ($target):
	case 'soas':
		$this->breadcrumbs=array(
			'Сервера'		=> array('soas/index'),
			$master->name	=> array('soas/view', 'id'=>$master->soas_id),
			'Добавить ролики'
		);
		
		$pageTitle = $master->name.': добавить ролики';
		break;
	case 'promo':
		$this->breadcrumbs=array(
			'Ролики'		=> array('promo/index'),
			$master->name	=> array('promo/view', 'id'=>$master->promo_id),
			'Привязть к серверам'
		);
		
		$pageTitle = $master->name.': привязать к серверам ролики';
		break;
endswitch;

$this->pageTitle = $pageTitle;

?>

<h1><?= $pageTitle ?></h1>

<?php if (count($detail) == 0): ?>
Нет доступных записей.
<?php else: ?>

<div class="form">

<?php $form=$this->beginWidget('CActiveForm', array(
	'id'=>'lnk-soas-promo-form',
	'enableAjaxValidation'=>false,
)); ?>
	
	
	<?php echo $form->hiddenField($model,$targetIdField); ?>
	
	<?php
		foreach ($detail as $rec):
			$fieldName = 'detail_ids['.$rec[$detailIdField].']';
			$fieldId = 'LnkSoasPromoForm_detail_ids_'.$rec[$detailIdField];
	?>
	<div class="row wide-row">
		<?php echo $form->checkBox($model,$fieldName); ?>
		<?php echo CHtml::label($rec->name, $fieldId); ?>
	</div>
	<?php endforeach; ?>
	
	<div class="row buttons">
		<?php echo CHtml::submitButton('Сохранить'); ?>
	</div>

<?php $this->endWidget(); ?>

</div><!-- form -->

<?php endif; ?>
