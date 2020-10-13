<?php
use Restserver\Libraries\REST_Controller;
defined('BASEPATH') OR exit('No direct script access allowed');

// This can be removed if you use __autoload() in config.php OR use Modular Extensions
/** @noinspection PhpIncludeInspection */
//To Solve File REST_Controller not found
require APPPATH . 'libraries/REST_Controller.php';
require APPPATH . 'libraries/Format.php';

class Clientes extends REST_Controller {

  	public function __construct(){
        parent::__construct();
        $this->load->model('Clients_model');
        $this->load->model('Address_model');
        $this->load->model('Contact_model');
    }


public function index_get() {
	// echo 'todos los usuarios';
		try {
			$query = $this->Clients_model->getClients('','Administrador');

			if($query != false){
				
				$clients = array(); // creo el array

				foreach ($query as $row) {

				   $clients[] =  $this->get_client($row['Cliente']);
				}	

				$this->response(array( 'Estado' => Status_OK,
			                           'Excepcion' => '',
			                           'Clientes' => $clients),200);
			} else{
				$this->response(array(  'Estado' => Status_FAIL,
										'Excepcion' => 'No hay clientes registrados',
										'Clientes' => null),200);
			}

		} catch (Exception $e) {
    		
 			$this->response(array('Estado' => Status_FAIL,
		                          'Excepcion' => $e->getMessage(),
		                          'Clientes' => null),200);
		}
	}


	public function find_get() {
	   
		try {

		    $GetParameter =  $this->input->get(NULL, TRUE); // returns all POST items with XSS filter

		    $search='';

		    if(isset($GetParameter['Search'])) {
		       $search =  strtoupper(trim($GetParameter['Search']));     
		    }
		    else{
		        $search = '';
		    }

	        $email = $GetParameter['EmailAgent'];
	        $profile = $GetParameter['Profile'];

		    $query = $this->Clients_model->find($email,$profile,$search,array(STATS_NEW,STATS_MOFIDY));

		    if($query != false) {
				$clients = array(); // creo el array

				foreach ($query as $row) {

				$clients[] =  $this->get_client($row['Cliente']);
			}	

			$this->response(array( 'Estado' => Status_OK,
			                       'Excepcion' => '',
			                       'Clientes' => $clients),200);
			} else{
			   $this->response(array(  'Estado' => Status_FAIL,
										'Excepcion' => 'No se encontraron clientes con el criterio de busqueda',
										'Clientes' => null),200);
		    }

		} catch (Exception $e) {
    		
 			$this->response(array('Estado' => Status_FAIL,
		                          'Excepcion' => $e->getMessage(),
		                          'Clientes' => null),200);
		}

	}


	public function update_post() {
	    	
    	try {
            $clientDataPost =  $this->input->post(NULL, TRUE); // returns all POST items with XSS filter

            $status = 0;
            $message='';
            $clientes=null;

		    if(!$clientDataPost) {

		        $status = Status_FAIL;
	        	$message='No se recibieron datos de clientes';
	         	$clientes=null;	
		    }
		    else {

		  		$client = $this->Clients_model->update($clientDataPost);

		        if($client != false) {
	           
		            $status = Status_OK;
	         		$message='Cliente actualizado satisfactoriamente';
	         		
	         		$clientes=array($this->get_client($client->Cliente));
		        }        
		        else {
		           
		            $status = Status_FAIL;
	         		$message='Internal Server Error';
	         		$clientes=null;
		        }
	        }

	        $this->response(array('Estado' =>  $status ,
			                      'Excepcion' => $message,
			                      'Clientes' => $clientes),200);

    	} catch (Exception $e) {
		
			$this->response(array('Estado' => Status_FAIL,
	                        		'Excepcion' => $e->getMessage(),
	                        		'Clientes' => null),200);
		}

    }

    public function saveloc_post() {
	    	
    	try {

	    	$requestPost =  $this->input->post(NULL, TRUE); // returns all POST items with XSS filter
		
		 	$Client = $requestPost['Client'];	
	    	$Address = $requestPost['Address'];

	    	$data=array('Longitud' => $requestPost['Longitude'],
	                    'Latitud' => $requestPost['Latitude'],
			            'LocEditable' => "0" );

	    	$row =  $this->Clients_model->saveLoc($Client,$Address,$data);

	    	$status = 0;
	        $message='';
	        $clientes=null;

			if($row != false) {

			    $clients[] =  $data;

			    $status = Status_OK;
	        	$message='Loc actualizado satisfactoriamente';
	        	$clientes=array($this->get_client($row->Cliente));

			} else{

				$status = Status_FAIL;
	        	$message='No hay clientes registrados';
	        	$clientes=null;
				
			}

			$this->response(array('Estado' => $status,
								  'Excepcion' => $message,
								  'Clientes' => $clientes),200);

		} catch (Exception $e) {
    		
 			$this->response(array('Estado' => Status_FAIL,
		                          'Excepcion' => $e->getMessage(),
		                          'Clientes' => null),200);
		}

    }


// Add new Clients
	public function index_post() {

		try {

			$clientDataPost =  $this->input->post(NULL, TRUE); // returns all POST items with XSS filter

			$status = 0;
	        $message='';
	        $clientes=null;

		    if(!$clientDataPost) {

		        $status = Status_FAIL;
	        	$message='No se recibieron datos de clientes';
	         	$clientes=null;	
		    }
		    else {

		  		$client = $this->Clients_model->save($clientDataPost);

		        if($client != false) {
	           
		            $status = Status_OK;
	         		$message='Cliente registrado satisfactoriamente';
	         		$clientes=array($client);	
		        }
		        else {
		           
		            $status = Status_FAIL;
	         		$message='Internal Server Error';
	         		$clientes=null;
		        }
	        }

	        $this->response(array('Estado' =>  $status ,
			                      'Excepcion' => $message,
			                      'Clientes' => $clientes),200);

    	} catch (Exception $e) {
		
			$this->response(array('Estado' => Status_FAIL,
	                        'Excepcion' => $e->getMessage(),
	                        'Clientes' => null),200);
		}

	}

	// List Clients
	public function list_post() {
	// echo 'todos los usuarios';

		try {

			$clientDataPost =  $this->input->post(NULL, TRUE); // returns all POST items with XSS filter

			$email = $clientDataPost['EmailAgent'];
			$profile = $clientDataPost['Profile'];

			$query = $this->Clients_model->getClients($email,$profile);
			
			if($query != false){
			
				$clients = array(); // creo el array
			
				foreach ($query as $row) {
			  
				   $clients[] =  $this->get_client($row['Cliente']);
				}

				$this->response(array( 'Estado' => Status_OK,
			                           'Excepcion' => '',
			                           'Clientes' => $clients),200);
			} else{
				$this->response(array(  'Estado' => Status_FAIL,
										'Excepcion' => 'No tienes clientes registrados',
										'Clientes' => null),200);
			}
		
		} catch (Exception $e) {
    		
 			$this->response(array('Estado' => Status_FAIL,
		                          'Excepcion' => $e->getMessage(),
		                          'Clientes' => null),200);
		}

	}

	private function get_client($clientId) {

    	$cliente =  $this->Clients_model->getClient($clientId);

    	$query_address = $this->Address_model->get($cliente->Cliente);
		$query_contact = $this->Contact_model->get($cliente->Cliente);

		if ($query_address === false){
	   		$query_address = null;
	    }

	    if ($query_contact === false){
			$query_contact = null;
		}

	    $data = array('Cliente' => $cliente->Cliente,
                      'Rfc' => $cliente->Rfc,
                      'Nombre' => $cliente->Nombre,
                      'FechaHora' => $cliente->FechaHora,
                      'CorreoAgente' => $cliente->CorreoAgente,
                      'Eliminado' => $cliente->Eliminado,
                      'Estatus' => $cliente->Estatus,
                      'Direccion' => $query_address,
                      'Contacto' =>  $query_contact
	                  );

	    return $data;
    }


}