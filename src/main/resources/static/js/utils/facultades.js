const MAPA_FACULTADES = {

    // Ciencias de la Computación y Diseño Web
    "SISTEMAS DE INFORMACIÓN":"Facultad de Ciencias de la Computación y Diseño Web",
    "SOFTWARE (REDISEÑO)":"Facultad de Ciencias de la Computación y Diseño Web",
    "TECNOLOGÍAS DE LA INFORMACIÓN":"Facultad de Ciencias de la Computación y Diseño Web",
    "TELEMÁTICA":"Facultad de Ciencias de la Computación y Diseño Web",

    // Ciencias Empresariales
    "ADMINISTRACIÓN DE EMPRESAS (REDISEÑO)":"Facultad de Ciencias Empresariales",
    "CONTABILIDAD Y AUDITORÍA (REDISEÑO)":"Facultad de Ciencias Empresariales",
    "GESTIÓN DEL TALENTO HUMANO":"Facultad de Ciencias Empresariales",
    "MERCADOTECNIA":"Facultad de Ciencias Empresariales",

    // Ciencias Pecuarias y Biológicas
    "ACUICULTURA":"Facultad de Ciencias Pecuarias y Biológicas",
    "BIOLOGÍA":"Facultad de Ciencias Pecuarias y Biológicas",
    "AGROPECUARIA":"Facultad de Ciencias Pecuarias y Biológicas",
    "ZOOTECNIA (REDISEÑO)":"Facultad de Ciencias Pecuarias y Biológicas",

    // Industria y Producción
    "AGROINDUSTRIA (REDISEÑO)":"Facultad de Ciencias de la Industria y Producción",
    "ALIMENTOS (REDISEÑO)":"Facultad de Ciencias de la Industria y Producción",
    "INGENIERÍA INDUSTRIAL (REDISEÑO)":"Facultad de Ciencias de la Industria y Producción",
    "SEGURIDAD INDUSTRIAL (REDISEÑO)":"Facultad de Ciencias de la Industria y Producción",

    // Agrarias y Forestales
    "AGROECOLOGÍA":"Facultad de Ciencias Agrarias y Forestales",
    "AGRONOMÍA (REDISEÑO)":"Facultad de Ciencias Agrarias y Forestales",
    "INGENIERÍA AGRÍCOLA":"Facultad de Ciencias Agrarias y Forestales",
    "INGENIERÍA FORESTAL":"Facultad de Ciencias Agrarias y Forestales",

    // Ingeniería
    "ARQUITECTURA":"Facultad de Ciencias de la Ingeniería",
    "ELECTRICIDAD":"Facultad de Ciencias de la Ingeniería",
    "HIDROLOGÍA":"Facultad de Ciencias de la Ingeniería",
    "INGENIERÍA AMBIENTAL":"Facultad de Ciencias de la Ingeniería",
    "INGENIERÍA CIVIL":"Facultad de Ciencias de la Ingeniería",
    "MECÁNICA (REDISEÑO)":"Facultad de Ciencias de la Ingeniería",

    // Educación
    "EDUCACIÓN INICIAL":"Facultad de Ciencias de la Educación",
    "EDUCACIÓN BÁSICA":"Facultad de Ciencias de la Educación",
    "PEDAGOGÍA DE LOS IDIOMAS NACIONALES Y EXTRANJEROS":"Facultad de Ciencias de la Educación",
    "PSICOPEDAGOGÍA":"Facultad de Ciencias de la Educación",

    // Salud
    "ENFERMERÍA":"Facultad de Ciencias de la Salud",

    // Sociales
    "ADMINISTRACIÓN PÚBLICA":"Facultad de Ciencias Sociales, Económicas y Financieras",
    "ECONOMÍA (REDISEÑO)":"Facultad de Ciencias Sociales, Económicas y Financieras",
    "FINANZAS":"Facultad de Ciencias Sociales, Económicas y Financieras",
    "TURISMO":"Facultad de Ciencias Sociales, Económicas y Financieras"
};

function obtenerFacultad(carrera){

    if(!carrera) return "";

    return MAPA_FACULTADES[carrera.toUpperCase()] || "";
}