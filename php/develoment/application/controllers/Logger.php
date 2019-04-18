<?php
use Restserver\Libraries\REST_Controller;
defined('BASEPATH') OR exit('No direct script access allowed');

//define("URL", 'http://localhost/pictures/');
define("URL", 'http://www.cadewigroup.com/pictures/');

// This can be removed if you use __autoload() in config.php OR use Modular Extensions
/** @noinspection PhpIncludeInspection */
//To Solve File REST_Controller not found
require APPPATH . 'libraries/REST_Controller.php';
require APPPATH . 'libraries/Format.php';

class Logger extends REST_Controller {


	public function __construct(){
        parent::__construct();
        $this->load->model('Logger_model');
    }


    public function index_get() {

    	try {

           	$GetParameter = $this->input->get(NULL, TRUE); // returns all POST items with XSS filter

			$id='';

			if(isset($GetParameter['id'])) {
				$id = trim($GetParameter['id']);     
			}
			else{
				$id = '';
			}

			$logger = $this->Logger_model->get($id);

			if(!is_null($logger) && !empty($logger)){
			  $this->response(array('Estado' => Status_OK,
			                        'Excepcion' => '',
			                        'Logger' => $logger),200);
			} else{
			  $this->response(array( 'Estado' => Status_OK,
			                         'Excepcion' => 'No hay log registrados',
			                         'Logger' => null),200);
			}

	   } catch (Exception $e) {
	    		
			$this->response(array('Estado' => Status_FAIL,
	                          	  'Excepcion' => $e->getMessage(),
	                          	  'Logger' => null),200);
	   }


	}


    public function search_get() {

    	try {

           	$GetParameter =  $this->input->get(NULL, TRUE); // returns all POST items with XSS filter

			$search='';

			if(isset($GetParameter['search'])) {
				$search = trim($GetParameter['search']);     
			}
			else{
				$search = '';
			}
			
			$logger = $this->Logger_model->search($search);

			if(!is_null($logger) && !empty($logger)){
			  $this->response(array('Estado' => Status_OK,
			                        'Excepcion' => '',
			                        'Logger' => $logger),200);
			} else{
			  $this->response(array( 'Estado' => Status_OK,
			                         'Excepcion' => 'No hay log registrados',
			                         'Logger' => null),200);
			}

	   } catch (Exception $e) {
	    		
			$this->response(array('Estado' => Status_FAIL,
	                          	  'Excepcion' => $e->getMessage(),
	                          	  'Logger' => null),200);
	   }
    }


    public function save_post() {
     
     	try {
		        $DataPost = $this->input->post(NULL, TRUE); // returns all POST items with XSS filter
				
		//	$this->response($this->_setLogger($DataPost),400);
		//	$this->response($this->$DataPost,400);

		        if(!$DataPost){
		             $this->response(array('Estado' => Status_FAIL,
		                                  'Excepcion' => 'Parametros no recibidos',
		                                  'Logger' => null),200);
		        }

		      	$parameters = $this->_setLogger($DataPost);


			//	$this->response($this->	$parameters,400);

		        $query = $this->Logger_model->save($parameters);

		        if($query != false){
		            $this->response(array('Estado' => Status_OK,
		                                  'Excepcion' => 'Logger recibido satisfactoriamente',
		                                  'Logger' => $query),200);
		        } else {
		            $this->response(array('Estado' => Status_FAIL,
		                                  'Excepcion' => 'Fallo en la referencia en base de datos',
		                                  'Logger' => null),200);
		        }

       	 	} catch (Exception $e) {
    		
 			$this->response(array('Estado' => Status_FAIL,
		                          'Excepcion' => $e->getMessage(),
		                          'Logger' => null),200);
		}

	}


	private function _setLogger($data){
		
		return array('Correo' => $data['UserEmail'],
		             'Tipo' => $data['Type'],
			     'Categoria' => $data['Category'],
			     'Dispositivo' => $data['Device'],
			     'Mensaje' => $data['Message']
			    );
	} 


}