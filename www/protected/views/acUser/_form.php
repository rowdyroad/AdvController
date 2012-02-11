<div class="form">

<?php $form=$this->beginWidget('CActiveForm', array(
	'id'=>'ac-user-form',
	'enableAjaxValidation'=>false,
)); ?>

	<p class="note">Поля, отмеченные <span class="required">*</span>, обязательны для заполнения.</p>

	<?php echo $form->errorSummary($model); ?>

	<div class="row">
		<?php echo $form->labelEx($model,'name'); ?>
		<?php echo $form->textField($model,'name',array('size'=>60,'maxlength'=>255)); ?>
		<?php echo $form->error($model,'name'); ?>
	</div>

	<div class="row">
		<?php echo $form->labelEx($model,'login'); ?>
		<?php echo $form->textField($model,'login',array('size'=>60,'maxlength'=>80)); ?>
		<?php echo $form->error($model,'login'); ?>
	</div>
	
	<?php if ($displayPasswordField === true): ?>
	<div class="row">
		<?php echo $form->labelEx($model,'password'); ?>
		<?php echo $form->passwordField($model,'password',array('size'=>60,'maxlength'=>80)); ?>
		<?php echo $form->error($model,'password'); ?>
	</div>
	<?php endif; ?>

	<div class="row wide-row">
		<?php echo $form->checkBox($model,'is_admin'); ?>
		<?php echo $form->labelEx($model,'is_admin'); ?>
		<?php echo $form->error($model,'is_admin'); ?>
	</div>

	<div class="row wide-row">
		<?php echo $form->checkBox($model,'is_observer'); ?>
		<?php echo $form->labelEx($model,'is_observer'); ?>
		<?php echo $form->error($model,'is_observer'); ?>
	</div>

	<div class="row buttons">
		<?php echo CHtml::submitButton($model->isNewRecord ? 'Создать' : 'Сохранить'); ?>
	</div>

<?php $this->endWidget(); ?>

</div><!-- form -->