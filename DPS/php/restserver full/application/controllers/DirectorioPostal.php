<?php

use Restserver\Libraries\REST_Controller;

defined('BASEPATH') OR exit('No direct script access allowed');

// This can be removed if you use __autoload() in config.php OR use Modular Extensions
/** @noinspection PhpIncludeInspection */
//To Solve File REST_Controller not found
require APPPATH . 'libraries/REST_Controller.php';
require APPPATH . 'libraries/Format.php';

class DirectorioPostal extends REST_Controller {

	public function __construct(){
        parent::__construct();
        $this->load->model('zipCode_model');
    }


	public function find_get($ZipCode = null) {
		
		try {
	 		$DataGet =  $this->input->get(NULL, TRUE); // returns all POST items with XSS filter
	 		$zipcode = $DataGet['ZipCode'];
	 		//$this->response(array($DataGet,$ZipCode));

			if(is_null($zipcode)) {
				$this->response(null,400);
			}

			$Directory = $this->zipCode_model->get($zipcode);

			if($Directory != false){
				$this->response(array('Estado' => Status_OK,
				                      'Excepcion' => 'Codigo postal identificado',	
				                      'DirectorioPostal' => $Directory),200);
			} else {
				$this->response(array('Estado' => Status_OK,
									  'Excepcion' => 'Codigo Postal no identificado',
				                      'DirectorioPostal' => null),200);
			}

		 } catch (Exception $e) {
	    		
			$this->response(array('Estado' => Status_FAIL,
	                          	  'Excepcion' => $e->getMessage(),
	                             'DirectorioPostal' => null),404);
		}
	}

}