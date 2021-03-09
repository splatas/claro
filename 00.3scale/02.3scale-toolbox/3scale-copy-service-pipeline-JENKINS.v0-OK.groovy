pipeline {
	agent any
	stages {
		stage('Setting parameters') {
			steps {
				script{
					// Service Token: CopiaBilletera	Service management API	Read & Write	9a09c2c8c24dfc4424f8903b7faa2a32bcd38f72a42aa5cc064c1c5d21b155fc
					// toolbox_token = "b81727d39397d3a8fca71bc64f25aee68fa29a816ac71cf32ba7641498430866"
					
					// NO FUNCIONA: env.ACCESS_TOKEN_DEV = "31b2147eab780e4961823ca1ef96a51c31d8cadeb19f98ddf89cc2b554d44900"
					env.ACCESS_TOKEN_DEV = "b81727d39397d3a8fca71bc64f25aee68fa29a816ac71cf32ba7641498430866" //dev_toolbox_token
					// NO FUNCIONA: env.ACCESS_TOKEN_TEST = "4f8e624c3cfe8b828bc88d6e3edd22cbacfcd5d424b0b2b4458cb88592a4f6e7"
					env.ACCESS_TOKEN_TEST = "2e2191d61cbfa1bed4d502678c8b0a6977df29110920f659fc86b4ff3083a7a0" //test_toolbox_token
					// NO FUNCIONA: env.ACCESS_TOKEN_PROD = "03b2e8b35c208a885e78da8d4d1d46c148c3b2748578ca1008a78dbf0284428f"
					env.ACCESS_TOKEN_PROD = "4b25111c4b0e413235f4a4bc04ce1e4778ab866c9cf3e54010c4209a83d6dc72"  //prod_toolbox_token
					
					env.URL_DEV = "dev-3scale-admin.apps.osepext01.claro.amx"
					env.URL_TEST = "test-3scale-admin.apps.osepext01.claro.amx"
					env.URL_PROD = "prod-3scale-admin.apps.osepext01.claro.amx"
					
					// ESTO FUNCIONA OK
					// 3scale application-plan list https://b81727d39397d3a8fca71bc64f25aee68fa29a816ac71cf32ba7641498430866@dev-3scale-admin.apps.osepext01.claro.amx 13 --insecure
					
					// env.dev_toolbox_token = "b81727d39397d3a8fca71bc64f25aee68fa29a816ac71cf32ba7641498430866"
					// echo "Comando de prueba => 3scale application-plan list https://${env.dev_toolbox_token}@${env.URL_DEV} 13 --insecure"
					// sh "3scale application-plan list https://${env.dev_toolbox_token}@${env.URL_DEV} 13 --insecure"
					
					
					// ESTO FUNCIONA OK
					// env.dev_toolbox_token = "b81727d39397d3a8fca71bc64f25aee68fa29a816ac71cf32ba7641498430866"
					// echo "Comando de Copy => 3scale application-plan list https://${env.dev_toolbox_token}@${env.URL_DEV} 13 --insecure"
					//sh "3scale copy service 13 --source=https://${env.dev_toolbox_token}@${env.URL_DEV} --destination=https://${env.dev_toolbox_token}@${env.URL_DEV} --target_system_name=srv13_copy_by_toolbox --insecure"
					
				}
			}
		}
		stage("Select Tenant") {
			steps {
				script{
					echo "URLS: URL_DEV=${URL_DEV}, URL_TEST=${URL_TEST}, URL_PROD=${URL_PROD}"
					echo "ACCESS_TOKEN_DEV: ${ACCESS_TOKEN_DEV}"
					echo "ACCESS_TOKEN_TEST: ${ACCESS_TOKEN_TEST}"
					echo "ACCESS_TOKEN_PROD: ${ACCESS_TOKEN_PROD}"
					
					env.TENANT_SOURCES = ""
					env.TENANT_DESTINATION = ""
					
					def INPUT_PARAMS = input message: 'Seleccione los Tenant de origen y destino', ok: 'Next',
					parameters: [
						choice(name: 'TENANT_SOURCES', choices: ['DEV','TEST','PROD'].join('\n'), description: 'Seleccione un TENANT origen'),
						choice(name: 'TENANT_DESTINATION', choices: ['DEV','TEST','PROD'].join('\n'), description: 'Seleccione un TENANT destino')
					]
					env.TENANT_SOURCES = INPUT_PARAMS.TENANT_SOURCES
					env.TENANT_DESTINATION = INPUT_PARAMS.TENANT_DESTINATION
				}
			}
		}
		stage("Select service") {
			steps {
				script{
					def INPUT_PARAMS = input message: 'Seleccione el servicio a promocionar', ok: 'Next',
					parameters: [
						choice(name: 'ID_SOURCES', choices: ['6:Billetera'].join('\n'), description: "Seleccione un ID de servicio origen en ${env.TENANT_SOURCES}"),
						string(name: 'SERVICE_NAME', defaultValue: 'None', description: "Nombre del servicio a promocionar a ${env.TENANT_DESTINATION}")
					]
					env.ID_SOURCES = INPUT_PARAMS.ID_SOURCES
					env.SERVICE_NAME = INPUT_PARAMS.SERVICE_NAME
				}
			}
		}
		stage("Copy Service") {
			steps {
				script{
					echo "All parameters have been set as Environment Variables"
					def idS = env.ID_SOURCES.split(":")[0].trim()
					echo "ID_SOURCES: ${idS}"
					echo "SERVICE_NAME: ${env.SERVICE_NAME}"
			
					def URL_SOURCE = env.TENANT_SOURCES == "PROD" ? env.URL_PROD : (env.TENANT_SOURCES == "DEV" ? env.URL_DEV : env.URL_TEST )
					echo "URL_SOURCE: ${URL_SOURCE}"
			
					def ACCESS_TOKEN_SOURCE = env.TENANT_SOURCES == "PROD" ? env.ACCESS_TOKEN_PROD : (env.TENANT_SOURCES == "DEV" ? env.ACCESS_TOKEN_DEV : env.ACCESS_TOKEN_TEST )
					echo "ACCESS_TOKEN_SOURCE: ${ACCESS_TOKEN_SOURCE}"
			
					def URL_DESTINATION = env.TENANT_DESTINATION == "PROD" ? env.URL_PROD : (env.TENANT_DESTINATION == "DEV" ? env.URL_DEV : env.URL_TEST )
					echo "URL_DESTINATION: ${URL_DESTINATION}"
			
					def ACCESS_TOKEN_DESTINATION = env.TENANT_DESTINATION == "PROD" ? env.ACCESS_TOKEN_PROD : (env.TENANT_DESTINATION == "DEV" ? env.ACCESS_TOKEN_DEV : env.ACCESS_TOKEN_TEST )
					echo "ACCESS_TOKEN_DESTINATION: ${ACCESS_TOKEN_DESTINATION}"
			
					
					// ESTO FUNCIONA OK
					// env.dev_toolbox_token = "b81727d39397d3a8fca71bc64f25aee68fa29a816ac71cf32ba7641498430866"
					// echo "Comando de Copy => 3scale application-plan list https://${env.dev_toolbox_token}@${env.URL_DEV} 13 --insecure"
					//sh "3scale copy service 13 --source=https://${env.dev_toolbox_token}@${env.URL_DEV} --destination=https://${env.dev_toolbox_token}@${env.URL_DEV} --target_system_name=srv13_copy_by_toolbox --insecure"
					
					sh "3scale copy service ${idS} --source=https://${ACCESS_TOKEN_SOURCE}@${URL_SOURCE} --destination=https://${ACCESS_TOKEN_DESTINATION}@${URL_DESTINATION} --target_system_name=${env.SERVICE_NAME} --insecure"
					
					
					
					// PRUEBAS
					//"3scale copy service 6 --source=https://${ACCESS_TOKEN_DEV}@$URL_DEV} --destination=https://${ACCESS_TOKEN_TEST}@${URL_TEST} --target_system_name=copia_billetera -k"
					// 3scale copy service 6 --source=https://${ACCESS_TOKEN_DEV}@$URL_DEV} --destination=https://${ACCESS_TOKEN_DEV}@${URL_DEV} --target_system_name=copia_billetera -k
					//
					// 3scale application-plan create [opts] <remote> <service> <plan-name>
					// 3scale application-plan create https://${ACCESS_TOKEN_DEV}@$URL_DEV} 13 CopiaBilletera
					 
				}
			}
			
			
		}
	}
}