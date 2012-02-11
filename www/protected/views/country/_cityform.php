<div class="form">
<?php $form=$this->beginWidget('CActiveForm', array(
	'id'=>'city-form',
	'enableAjaxValidation'=>false,
)); ?>


    <p class="note">Поля, отмеченные <span class="required">*</span>, обязательны для заполнения.</p>
    <?php echo $form->errorSummary($model);?>
    <? echo $form->hiddenField($model,'country_id');?>
	<div class="row">
		<?php echo $form->labelEx($model,'name'); ?>
		<?php echo $form->textField($model,'name',array('size'=>60,'maxlength'=>255)); ?>
		<?php echo $form->error($model,'name'); ?>
	</div>
    <div class="row">
		<?php echo $form->labelEx($model,'timezone'); ?>
		<?php echo $form->textField($model,'timezone',array('size'=>60,'maxlength'=>255)); ?>
		<?php echo $form->error($model,'timezone'); ?>
	</div>
	<div class="row buttons">
	<?php echo CHtml::submitButton($model->isNewRecord ? 'Добавить' : 'Изменить'); ?>
	</div>
<?php $this->endWidget(); ?>
</div><!-- form -->