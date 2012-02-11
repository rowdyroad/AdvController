<?php

class PromoController extends Controller
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
				'actions'=>array('index','view','idents'),
				'users'=>array('@'),
			),
			array('allow', // allow admin user to perform 'admin' and 'delete' actions
				'actions'=>array('create','update','delete','idents','upload','mark','check','add','activate','deactivate'),
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
		$model=new Promo('create');

		// Uncomment the following line if AJAX validation is needed
		// $this->performAjaxValidation($model);

		if (isset($_POST['Promo']))
		{
			$model->attributes=$_POST['Promo'];
			$model->file=CUploadedFile::getInstance($model,'file');
			
			//$transaction = $model->dbConnection->beginTransaction();
			
			if($model->save())
			{
				if ($model->file !== null)
					$this->uploadPromoFile($model);
				
				if ($model->getError('file') === null)
				{
					//$transaction->commit();
					$this->redirect(array('view','id'=>$model->promo_id));
				}
				else
				{
					//$transaction->rollBack();
					$model->delete();
				}
			}
		}

		$this->render('create',array(
			'model'=>$model,
		));
	}
	
	public function uploadPromoFile($model)
	{
/*		$uploaderRetVal = 0;
        $model->sha1 = system('/sbin/sha1 -q '.$model->file->tempName);
		$command = Yii::app()->params['promo_loader_path'].' '.$model->file->tempName.' '.Yii::app()->params['promos_path'].'/'.$model->promo_id.' '.$model->promo_id.' 1>/dev/null';
        system($command, $uploaderRetVal);
		unlink($model->file->tempName);

		if ($uploaderRetVal !== 0)
        {
			$model->addError('file', 'Не удалось загрузить файл. Код ошибки: '.$uploaderRetVal);
        }
        else $model->save();*/
        
        
        if ($model->file->error != 0)
        {
    	    $model->addError('file','Ошибка загрузки файла('.$model->file->error.')');
    	    return;
        }
        
        if (!preg_match("/audio\/.*wav.*/",$model->file->type))
        {
    	    $model->addError('file','Неверный формат файла');
            return;
        }

        $src = $model->file->tempName;
        $dst = Yii::app()->params['promos_path'].'/'.$model->promo_id.'.wav';        
        system("/usr/local/bin/sox $src -r 44100 -b 16 $dst silence 1 0 -40d trim 00:01 reverse silence 1 0 -40d trim 00:01 reverse 2>>/tmp/sox.log");
        unlink($src);        
        $model->save();
        
	}
    
    public function actionUpload()
    {
            Yii::import("ext.EAjaxUpload.qqFileUploader");     
            $folder='upload/';// folder for uploaded files
            $allowedExtensions = array("wav");//array("jpg","jpeg","gif","exe","mov" and etc...
            $sizeLimit = 30 * 1024 * 1024 * 1024;// maximum file size in bytes
            $uploader = new qqFileUploader($allowedExtensions, $sizeLimit);
            $result = $uploader->handleUpload($folder);
            $result=htmlspecialchars(json_encode($result), ENT_NOQUOTES);
            echo $result;// it's array
            Yii::app()->end();
    }
    
    
    
    public function actionAdd()
    {
        if ($r = Yii::app()->db->createCommand("insert into promo_idents (`ident`,`length`,`name`,`hash`) values(:ident,:length,:name,:hash)")
        ->bindParam(":ident",$_REQUEST['ident'])
        ->bindParam(":length",$_REQUEST['length'])
        ->bindParam(":name",$_REQUEST['name'])
        ->bindParam(":hash",$_REQUEST['hash'])->execute()) 
        {
            echo CJSON::encode(array("result"=>"success"));
        } else {
            echo CJSON::encode(array("result"=>"error","msg"=>"file not found"));
        }
        return;       
    }
    
    public function actionMark()
    {
        $data = Array();
        $path = realpath($_REQUEST['filename']);
        $hash = sha1_file($path);         
        $pi = pathinfo($path);
        $filename = $pi['basename'];                   
        if ($length = Marker::GetTimeLength($path)) { 
            if ($ident = Marker::GetNewId($length)) {            
                $new_filename = "files/$filename";
                if (Marker::Mark($path, $new_filename,$ident)) {
                    $data["ident"] = $ident;
                    $data["length"] = $length;
                    $data["hash"] = $hash;
                    $data["filename"] = $new_filename;
                    $data["name"] = substr($filename, 0, strlen($filename) - 4);
                }
            }
        }        
        echo CJSON::encode(array("result"=>"success","data"=>$data));
        Yii::app()->end();

    }
    
    public function actionCheck()
    {
        $data = Array();        
        $filename =  realpath($_REQUEST['filename']);
        if (!file_exists($filename)) {
            echo CJSON::encode(array("result"=>"error","msg"=>"file not found"));
            Yii::app()->end();
            return;
        }
        
        if ($r = Yii::app()->db->createCommand("select * from promo_idents where hash = :hash")->bindParam(":hash",sha1_file($filename))->queryRow()) {        
            echo CJSON::encode(array("result"=>"error","msg"=>"file already exists"));
            Yii::app()->end();
            return;
        }
        
        if ($ident = Marker::Detect($filename)) {
            $data["ident"] = $ident;
            if ($r = Yii::app()->db->createCommand("select * from promo_idents where ident = :ident")->bindParam(":ident",$ident)->queryRow())
            {
                $data["promo"] = $r['name'];
            }
        }
        echo CJSON::encode(array("result"=>"success","data"=>$data));
        Yii::app()->end();
    }

	public function actionUpdate($id)
	{  
		$model=$this->loadModel($id);

		// Uncomment the following line if AJAX validation is needed
		// $this->performAjaxValidation($model);

		if(isset($_POST['Promo']))
		{
			$model->attributes=$_POST['Promo'];
			$model->file=CUploadedFile::getInstance($model,'file');
			
			if($model->save())
			{
				if ($model->file !== null)
					$this->uploadPromoFile($model);
				
				if ($model->getError('file') === null)
					$this->redirect(array('view','id'=>$model->promo_id));
			}
		}

		$this->render('update',array(
			'model'=>$model
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
			PromoIdent::model()->deleteByPk($id);
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
		$model=new Promo('search');
		$model->unsetAttributes();  // clear any default values
		if(isset($_GET['Promo']))
			$model->attributes=$_GET['Promo'];
		
		if (!Yii::app()->user->is_admin)
			$this->layout = '//layouts/column1';

		$this->render('index',array(
			'model'=>$model,
		));
	}

	public function actionIdents()
	{
		$this->render('idents',array(
			'model'=>new PromoIdent(),
		));
	}
    
    public function actionDeactivate($id)
    {        
        PromoIdent::model()->updateByPk($id, array("actived"=>0));     
        $this->redirect($this->createUrl('promo/idents'));        
    }
    
    public function actionActivate($id)
    {
        if ($ident = PromoIdent::model()->findByPk($id)) {
             PromoIdent::model()->updateAll(array('actived'=>0),"ident = {$ident->ident}");
             $ident->actived = 1;
             $ident->update();  
        }        
        $this->redirect($this->createUrl('promo/idents'));
    } 
    
	public function loadModel($id)
	{
		$model=Promo::model()->findByPk((int)$id);
		if($model===null)
			throw new CHttpException(404,'The requested page does not exist.');
		return $model;
	}

	protected function performAjaxValidation($model)
	{
		if(isset($_POST['ajax']) && $_POST['ajax']==='promo-form')
		{
			echo CActiveForm::validate($model);
			Yii::app()->end();
		}
	}
}
