<?php

class LnkSoasPromoController extends Controller
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
			array('allow', // allow admin user to perform 'admin' and 'delete' actions
				'actions'=>array('create','delete'),
				'users'=>array('admin'),
			),
			array('deny',  // deny all users
				'users'=>array('*'),
			),
		);
    }

    /**
     * Creates a new model.
     * If creation is successful, the browser will be redirected to the 'view' page.
     */
    public function actionCreate()
    {
		$target = null;
		
		if ( isset($_REQUEST['soas_id']) ):
			$target = 'soas';
			$targetIdField = 'soas_id';
			$detailIdField = 'promo_id';
		elseif ( isset($_REQUEST['promo_id']) ):
			$target = 'promo';
			$targetIdField = 'promo_id';
			$detailIdField = 'soas_id';
		endif;
		
		if ($target === null)
			throw new CHttpException(400,'Неверный запрос');
			
        $model=new LnkSoasPromoForm($target.'_id',$detailIdField);

        // Uncomment the following line if AJAX validation is needed
        // $this->performAjaxValidation($model);
        if(isset($_POST['LnkSoasPromoForm']))
        {
            $model->attributes=$_POST['LnkSoasPromoForm'];
			
			//echo '<pre>'; print_r($model); exit;
			
            if($model->createLinks($targetIdField, $detailIdField))
            {              
                $this->redirect(array($target.'/view','id'=>$model[$targetIdField]));                
            }
        }
		else
		{
			$master = null;
			$detail = null;
			
			switch ($target):
				case 'soas':
					$master = Soas::model()->findByPk( $_GET['soas_id'] );
					if ($master === null)
						throw new CHttpException(404,'Сервер не найден');

					$model['soas_id'] = $_GET['soas_id'];
					
					$detail = Promo::model()->findALL(
							'promo_id NOT IN 
								(SELECT promo_id FROM lnk_soas_promo WHERE soas_id=:soas_id)',
							array(':soas_id'=>$_GET['soas_id']));
					
					break;
				case 'promo':
					$master = Promo::model()->findByPk( $_GET['promo_id'] );
					if ($master === null)
						throw new CHttpException(404,'Ролик не найден');

					$model['promo_id'] = $_GET['promo_id'];
					
					$detail = Soas::model()->findALL(
							'soas_id NOT IN 
								(SELECT soas_id FROM lnk_soas_promo WHERE promo_id=:promo_id)',
							array(':promo_id'=>$_GET['promo_id']));
										
					break;
			endswitch;
		}

        $this->render('create',array(
            'model'			=> $model,
			'target'		=> $target,
			'targetIdField'	=> $targetIdField,
			'detailIdField' => $detailIdField,
			'master'		=> $master,
			'detail'		=> $detail
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
            unlink($_SERVER['DOCUMENT_ROOT']."/finger_prints/$id");

            // if AJAX request (triggered by deletion via admin grid view), we should not redirect the browser
            if(!isset($_GET['ajax']))
                $this->redirect(isset($_POST['returnUrl']) ? $_POST['returnUrl'] : array('soas/index'));
        }
        else
            throw new CHttpException(400,'Invalid request. Please do not repeat this request again.');
    }
	
    /**
     * Returns the data model based on the primary key given in the GET variable.
     * If the data model is not found, an HTTP exception will be raised.
     * @param integer the ID of the model to be loaded
     */
    public function loadModel($id)
    {
        $model=LnkSoasPromo::model()->findByPk((int)$id);
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
        if(isset($_POST['ajax']) && $_POST['ajax']==='lnk-soas-promo-form')
        {
            echo CActiveForm::validate($model);
            Yii::app()->end();
        }
    }
}