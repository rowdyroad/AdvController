<?php

class AcUserController extends Controller
{
	/**
	 * @var string the default layout for the views. Defaults to '//layouts/column2', meaning
	 * using two-column layout. See 'protected/views/layouts/column2.php'.
	 */
	public $layout='//layouts/column2';

	/**
	 * @return array action filters
	 */
	public function filters()
	{
		return array(
			'accessControl', // perform access control for CRUD operations
		);
	}

	/**
	 * Specifies the access control rules.
	 * This method is used by the 'accessControl' filter.
	 * @return array access control rules
	 */
	public function accessRules()
	{
		return array(
			array('allow', // allow authenticated user to perform 'create' and 'update' actions
				'actions'=>array('index','view','create','update','updatePassword','delete'),
				'users'=>array('admin'),
			),
			array('deny',  // deny all users
				'users'=>array('*'),
			),
		);
	}

	/**
	 * Displays a particular model.
	 * @param integer $id the ID of the model to be displayed
	 */
	public function actionView($id)
	{
		$this->render('view',array(
			'model'=>$this->loadModel($id),
		));
	}

	/**
	 * Creates a new model.
	 * If creation is successful, the browser will be redirected to the 'view' page.
	 */
	public function actionCreate()
	{
		$model=new AcUser;

		// Uncomment the following line if AJAX validation is needed
		// $this->performAjaxValidation($model);

		if(isset($_POST['AcUser']))
		{
			$_POST['AcUser']['password'] = md5($_POST['AcUser']['password']);
			$model->attributes=$_POST['AcUser'];
			
			if($model->save())
				$this->redirect(array('view','id'=>$model->ac_user_id));
		}

		$this->render('create',array(
			'model'=>$model,
		));
	}
	
	/*static public function hasNetwork($network_id) 
	{
	    return  Yii::app()->db->createCommand('select 1 from users_networks where network_id = '.$network_id.' and user_id = '.Yii::app()->user->id)->queryScalar();
	}*/

	/**
	 * Updates a particular model.
	 * If update is successful, the browser will be redirected to the 'view' page.
	 * @param integer $id the ID of the model to be updated
	 */
	public function actionUpdate($id)
	{
		$model=$this->loadModel($id);

		// Uncomment the following line if AJAX validation is needed
		// $this->performAjaxValidation($model);

		if(isset($_POST['AcUser']))
		{
			$model->attributes=$_POST['AcUser'];
			if($model->save()) {
				Yii::app()->db->createCommand("delete from users_networks where user_id = ".$model->ac_user_id)->execute();
				$networks = @$_POST['User']['networks'];
				foreach ($networks as $id=>$o) {
				    Yii::app()->db->createCommand("insert into users_networks (user_id, network_id) values('".$model->ac_user_id."',$id)")->execute();
				}
				$this->redirect(array('view','id'=>$model->ac_user_id));
			}
		}

		$this->render('update',array(
			'model'=>$model,
		));
	}
	
	public function actionUpdatePassword($id)
	{
		$model=$this->loadModel($id);

		// Uncomment the following line if AJAX validation is needed
		// $this->performAjaxValidation($model);

		if(isset($_POST['AcUser']))
		{
			$_POST['AcUser']['password'] = md5($_POST['AcUser']['password']);
			$model->attributes=$_POST['AcUser'];
			if($model->save())
				$this->redirect(array('view','id'=>$model->ac_user_id));
		}

		$model->password = '';
		$this->render('updatePassword',array(
			'model'=>$model,
		));
	}

	/**
	 * Deletes a particular model.
	 * If deletion is successful, the browser will be redirected to the 'index' page.
	 * @param integer $id the ID of the model to be deleted
	 */
	public function actionDelete($id)
	{
		if(Yii::app()->request->isPostRequest)
		{
			// we only allow deletion via POST request
			$this->loadModel($id)->delete();

			// if AJAX request (triggered by deletion via index grid view), we should not redirect the browser
			if(!isset($_GET['ajax']))
				$this->redirect(isset($_POST['returnUrl']) ? $_POST['returnUrl'] : array('index'));
		}
		else
			throw new CHttpException(400,'Invalid request. Please do not repeat this request again.');
	}

	/**
	 * Manages all models.
	 */
	public function actionIndex()
	{
		$model=new AcUser('search');
		$model->unsetAttributes();  // clear any default values
		if(isset($_GET['AcUser']))
			$model->attributes=$_GET['AcUser'];

		$this->render('index',array(
			'model'=>$model,
		));
	}

	/**
	 * Returns the data model based on the primary key given in the GET variable.
	 * If the data model is not found, an HTTP exception will be raised.
	 * @param integer the ID of the model to be loaded
	 */
	public function loadModel($id)
	{
		$model=AcUser::model()->findByPk((int)$id);
		if($model===null)
			throw new CHttpException(404,'The requested page does not exist.');
		return $model;
	}

	/**
	 * Performs the AJAX validation.
	 * @param CModel the model to be validated
	 */
	protected function performAjaxValidation($model)
	{
		if(isset($_POST['ajax']) && $_POST['ajax']==='ac-user-form')
		{
			echo CActiveForm::validate($model);
			Yii::app()->end();
		}
	}
}
