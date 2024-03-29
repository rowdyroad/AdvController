<?php

class SoasController extends Controller
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
				'actions'=>array('index','view'),
				'users'=>array('@'),
			),
			array('allow', // allow admin user to perform 'admin' and 'delete' actions
				'actions'=>array('create','update','delete'),
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
        $model = new SoasView;
		$this->render('view',array('model'=>$model->findByAttributes(array('soas_id'=>$id))));
	}
    
	/**
	 * Creates a new model.
	 * If creation is successful, the browser will be redirected to the 'view' page.
	 */
	public function actionCreate()
	{
		$model=new Soas;

		// Uncomment the following line if AJAX validation is needed
		// $this->performAjaxValidation($model);

		if(isset($_POST['Soas']))
		{ 
            if (intval($_POST['Soas']['soas_id']) == 0) 
            {
                unset($_POST['Soas']['soas_id']);            
            }
            
           	$model->attributes=$_POST['Soas'];
            if (Soas::model()->findByPk($model->soas_id) != null)
            {
                    $model->addError("soas_id","Данный идентификатор уже зарегистрирован");
            }
            else
            {                   
                if (Soas::model()->findByAttributes(array("cinema_id"=>$model->cinema_id,"hall"=>$model->hall)) != null)
                {
                    $model->addError("hall","Данный номер зала текущего кинотеатра уже присудствует в системe");
                }
                else
                {
	               if($model->save())
                   {   
                    if (isset($_REQUEST['id']) && intval($_REQUEST['id']) > 0)
                    {
                           $this->redirect(array('network/viewCinema','id'=>intval($_REQUEST['id'])));
                    }
                    else
                    {
    				    $this->redirect(array('index'));
                    }                
                   }
                }                
            }            
		}
		$this->render('create',array('model'=>$model));
	}

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

		if(isset($_POST['Soas']))
		{
			$model->attributes=$_POST['Soas'];
			if($model->save())
				$this->redirect(array('view','id'=>$model->soas_id));
		}

		$this->render('update',array(
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
			LnkSoasPromo::deleteBySoasId($id);
			$this->loadModel($id)->delete();

			// if AJAX request (triggered by deletion via index grid view), we should not redirect the browser
			if(!isset($_GET['ajax']))
				$this->redirect(isset($_POST['returnUrl']) ? $_POST['returnUrl'] : array('index'));
		}
		else
			throw new CHttpException(400,'Invalid request. Please do not repeat this request again.');
	}

	/**
	 * Lists all models.
	 */
	public function actionIndex()
	{
		if (!Yii::app()->user->is_admin)
			$this->layout = '//layouts/column1';
		$this->render('index',array('model'=>new SoasView));
	}

	/**
	 * Returns the data model based on the primary key given in the GET variable.
	 * If the data model is not found, an HTTP exception will be raised.
	 * @param integer the ID of the model to be loaded
	 */
	public function loadModel($id)
	{
		$model=Soas::model()->findByPk((int)$id);
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
		if(isset($_POST['ajax']) && $_POST['ajax']==='soas-form')
		{
			echo CActiveForm::validate($model);
			Yii::app()->end();
		}
	}
}
