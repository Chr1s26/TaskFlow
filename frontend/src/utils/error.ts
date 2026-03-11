export function getErrorMessage(err:any){

  if(err.response?.data?.message){
    return err.response.data.message
  }

  if(err.response?.data?.error){
    return err.response.data.error
  }

  if(err.message){
    return err.message
  }

  return "Unexpected error occurred"
}