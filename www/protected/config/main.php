<?php

// uncomment the following to define a path alias
// Yii::setPathOfAlias('local','path/to/local-folder');

// This is the main Web application configuration. Any writable
// CWebApplication properties can be configured here.
return array(
	'basePath'=>dirname(__FILE__).DIRECTORY_SEPARATOR.'..',
	'name'=>'Консоль управления',
	// preloading 'log' component
	'preload'=>array('log'),

	// autoloading model and component classes
	'import'=>array(
		'application.models.*',
		'application.components.*',
	),

	'modules'=>array(
		// uncomment the following to enable the Gii tool
//		/*
		'gii'=>array(
			'class'=>'system.gii.GiiModule',
			'password'=>'1q2w3e4r'
		),
//		*/
	),

	// application components
	'components'=>array(
		'user'=>array(
			// enable cookie-based authentication
			'allowAutoLogin'=>true,
        //    'class'=>'WebUser'
		),
        'signature' => Array('class' => 'application.components.RequestSignature'),
		// uncomment the following to enable URLs in path-format
		/*
		'urlManager'=>array(
			'urlFormat'=>'path',
			'rules'=>array(
				'<controller:\w+>/<id:\d+>'=>'<controller>/view',
				'<controller:\w+>/<action:\w+>/<id:\d+>'=>'<controller>/<action>',
				'<controller:\w+>/<action:\w+>'=>'<controller>/<action>',
			),
		),
		*/
		
		'db'=>array(
			'connectionString' => 'mysql:host=localhost;dbname=soas',
			'emulatePrepare' => true,
			'username' => 'soas',
			'password' => '1q2w3e4r',
			'charset' => 'utf8',
			//'enableParamLogging' => true,
			//'enableProfiling' => true,
		//'schemaCachingDuration'=>3600
		),
		'errorHandler'=>array(
			// use 'site/error' action to display errors
            'errorAction'=>'site/error',
        ),
		'log'=>array(
			'class'=>'CLogRouter',
			'routes'=>array(
				array(
					'class'=>'CFileLogRoute',
					//'levels'=>'error, warning',
					'levels'=>'trace, info, error, warning',
				),
				/*
				array(
					'class' => 'CProfileLogRoute',
					'report' => 'summary',
				),
				*/
				// uncomment the following to show log messages on web pages
				/*
				array(
					'class'=>'CWebLogRoute',
				),
				*/
			),
		),
		
		'clientScript'=>array(
			  'scriptMap'=>array(
				  'jquery.min.js'		=> 'http://yandex.st/jquery/1.4.2/jquery.min.js',
				  'jquery.js'			=> 'http://yandex.st/jquery/1.4.2/jquery.js',
				  'jquery-ui.min.js'	=> 'http://yandex.st/jquery-ui/1.8.2/jquery-ui.min.js',
				  'jquery-ui.js'		=> 'http://yandex.st/jquery-ui/1.8.2/jquery-ui.js'
			  ),
		),
	),
	
	'sourceLanguage' => 'en',

	// application-level parameters that can be accessed
	// using Yii::app()->params['paramName']
	'params'=>array(
		'capturer' => '/usr/local/bin/java -jar /home/admin/data/bin/capturer.jar -c /home/admin/data/etc/config.ini ',
        'secret_key'=>'dps09arbjv4q$TFGQ#Bb-943g_gwwgsafg04ATg',
        'promos_path'=>'files'
	),
);