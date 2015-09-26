package project
import project.models._
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

trait CaseEnumSerializer[T <: CaseEnum] {
  def toString(value: T): Option[String]
  def fromString(str: Option[String]): T
}

object CaseEnumMacro {
  implicit def caseEnumSerializer[T <: CaseEnum]: CaseEnumSerializer[T] =
    macro CaseEnumMacro.caseEnumSerializerMacro[T]

  def caseEnumSerializerMacro[T <: CaseEnum : c.WeakTypeTag](c: Context): c.Tree = {
    import c.universe._
    val tpe = weakTypeOf[T]
    val typeName = tpe.typeSymbol
    val companion = tpe.typeSymbol.companion
    val enumElements = tpe.typeSymbol.companion.typeSignature.decls.collect {
      case x: ModuleSymbol => x
    } toList
    val undefined = enumElements.filter { x => x.name.decodedName.toString == "Undefined" } headOption

    undefined map { x =>
      if (x.typeSignature.baseClasses.filter { y => y == weakTypeOf[UndefinedCase].typeSymbol } isEmpty) {
        throw new Exception("The Undefined case should extend UndefinedCase")
      }
    }

    val noneValue = undefined match {
      case Some(_) => q"$companion.Undefined"
      case None => q"throw new Exception()"
    }
    val toString = undefined match {
      case Some(_) => q"""value match {
                        case $companion.Undefined => None
                        case _ => Some(map(value))
                      }"""
      case None => q"""Some(map(value))"""
    }
    val mapComponents = enumElements.filter { x => x.name.decodedName.toString != "Undefined" } map { x =>
      val name = x.name
      val decoded = name.decodedName.toString
      q"($companion.$name, $decoded)"
    }
    q"""
      new CaseEnumSerializer[$typeName] {
        private val map: Map[$typeName, String] = Map(..$mapComponents)
        private val revMap = map.map(_ swap)
        def toString(value: $typeName): Option[String] = $toString
        def fromString(opt: Option[String]): $typeName = opt match {
          case Some(str) => revMap(str)
          case None => $noneValue
        }
      }
    """
  }
}
