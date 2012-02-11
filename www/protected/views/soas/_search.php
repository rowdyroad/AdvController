<div class="wide form">

<?php $form=$this->beginWidget('CActiveForm', array(
	'action'=>Yii::app()->createUrl($this->route),
	'method'=>'get',
)); ?>

	<div class="row">
		<?php echo $form->label($model,'soas_id'); ?>
		<?php echo $form->textField($model,'soas_id'); ?>
	</div>

	<div class="row">
		<?php echo $form->label($model,'name'); ?>
		<?php echo $form->textField($model,'name',array('size'=>60,'maxlength'=>255)); ?>
	</div>
	<div class="row">
        <?php echo $form->label($model,'cinema_id'); ?>
        <?php echo $form->dropDownList(
						$model,'cinema_id',
						array( '' => '- выберите кинотеатр -' ) + CHtml::listData(Cinema::model()->findAll(), 'cinema_id', 'name')
						
		); ?>
    </div>
	<div class="row">
		<?php echo $form->label($model,'comments'); ?>
		<?php echo $form->textArea($model,'comments',array('rows'=>6, 'cols'=>50)); ?>
	</div>

	<div class="row buttons">
		<?php echo CHtml::submitButton('Поиск'); ?>
	</div>

<?php $this->endWidget(); ?>

</div><!-- search-form -->