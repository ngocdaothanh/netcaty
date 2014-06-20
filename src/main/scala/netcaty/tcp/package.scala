package netcaty

package object tcp {
  type RequestHandler  = Array[Byte] => Array[Byte]
  type ResponseHandler = Array[Byte] => Unit
}
